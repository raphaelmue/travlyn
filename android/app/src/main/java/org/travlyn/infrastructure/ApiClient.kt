package org.travlyn.infrastructure

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.travlyn.api.model.User
import org.travlyn.infrastructure.error.*
import org.travlyn.local.Application
import org.travlyn.local.LocalStorage
import java.io.File
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

open class ApiClient(
    val baseUrl: String = "http://travlyn.raphael-muesseler.de/travlyn/travlyn/1.0.0/",
    open val application: Application
) {
    companion object {
        protected const val ContentType = "Content-Type"
        protected const val Accept = "Accept"
        protected const val JsonMediaType = "application/json"
        protected const val FormDataMediaType = "multipart/form-data"
        protected const val XmlMediaType = "application/xml"

        @JvmStatic
        val client: OkHttpClient =
            OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS).build()

        @JvmStatic
        var defaultHeaders: Map<String, String> by ApplicationDelegates.setOnce(
            mapOf(
                ContentType to JsonMediaType,
                Accept to JsonMediaType
            )
        )

        @JvmStatic
        val jsonHeaders: Map<String, String> =
            mapOf(ContentType to JsonMediaType, Accept to JsonMediaType)
    }

    protected inline fun <reified T> requestBody(
        content: T,
        mediaType: String = JsonMediaType
    ): RequestBody {
        when {
            content is File -> {
                return content
                    .asRequestBody(mediaType.toMediaTypeOrNull())
            }
            mediaType == FormDataMediaType -> {
                var builder = FormBody.Builder()
                // content's type *must* be Map<String, Any>
                @Suppress("UNCHECKED_CAST")
                (content as Map<String, String>).forEach { (key, value) ->
                    builder = builder.add(key, value)
                }
                return builder.build()
            }
            mediaType == JsonMediaType -> {
                return Gson().toJson(content).toRequestBody(mediaType.toMediaTypeOrNull())
            }
            else -> {
                throw UnsupportedOperationException("Other formats than JSON cannot be converted to objects as of now.")
            }
        }
    }

    protected inline fun <reified T : Any?> responseBody(
        result: String?,
        mediaType: String = JsonMediaType
    ): T? {
        if (result == null) return null
        return when (mediaType) {
            JsonMediaType -> {
                GsonBuilder().create().fromJson(result, T::class.java)
            }
            else -> {
                throw UnsupportedOperationException("Other formats than JSON cannot be converted to objects as of now.")
            }
        }
    }

    protected suspend inline fun <reified T : Any?> request(
        requestConfig: RequestConfig,
        body: Any? = null
    ): T? {
        val httpUrl =
            baseUrl.toHttpUrlOrNull() ?: throw IllegalStateException("baseUrl is invalid.")

        var urlBuilder = httpUrl.newBuilder()
            .addPathSegments(requestConfig.path.trimStart('/'))

        requestConfig.query.forEach { query ->
            query.value.forEach { queryValue ->
                urlBuilder = urlBuilder.addQueryParameter(query.key, queryValue)
            }
        }

        val url = urlBuilder.build()
        val headers = requestConfig.headers + defaultHeaders

        if (headers[ContentType] ?: "" == "") {
            throw kotlin.IllegalStateException("Missing Content-Type header. This is required.")
        }

        if (headers[Accept] ?: "" == "") {
            throw kotlin.IllegalStateException("Missing Accept header. This is required.")
        }

        val contentType = (headers[ContentType] as String).substringBefore(";").toLowerCase()
        val accept = (headers[Accept] as String).substringBefore(";").toLowerCase()

        var request: Request.Builder = when (requestConfig.method) {
            RequestMethod.DELETE -> Request.Builder().url(url).delete()
            RequestMethod.GET -> Request.Builder().url(url)
            RequestMethod.HEAD -> Request.Builder().url(url).head()
            RequestMethod.PATCH -> Request.Builder().url(url).patch(requestBody(body, contentType))
            RequestMethod.PUT -> Request.Builder().url(url).put(requestBody(body, contentType))
            RequestMethod.POST -> Request.Builder().url(url).post(requestBody(body, contentType))
            RequestMethod.OPTIONS -> Request.Builder().url(url).method("OPTIONS", null)
        }

        headers.forEach { header -> request = request.addHeader(header.key, header.value) }
        val localStorage = LocalStorage(application.getContext())
        if (localStorage.contains("user")) {
            request.addHeader(
                "Authorization",
                "Bearer " + localStorage.readObject<User>("user")!!.token!!.token
            )
        }

        val content: String?
        val response: Response?
        val realRequest = request.build()

        try {
            response = client.newCall(realRequest).await()

            content = withContext(Dispatchers.IO) {
                response.body?.string()
            }

            if (response.isSuccessful) {
                return responseBody(content, accept)
            }

            responseError(realRequest, response)
        } catch (exception: Exception) {
            when (exception) {
                is SocketException, is SocketTimeoutException -> application.showErrorDialog(
                    ServerNotAvailableException(
                        "Socket connection timed out. Either server is not available or user has no internet connection."
                    )
                )
                is TravlynException -> {
                    application.showErrorDialog(exception)
                }
            }
        }

        return null
    }

    protected fun responseError(request: Request, response: Response) {
        val prefix = "[${request.method}: ${request.url}] --> ${response.code}: "
        when {
            response.code == 403 -> throw UnauthorizedException(prefix + "User is unauthorized to perform this action.")
            response.code == 404 -> throw NotFoundException(prefix + "Resource was not found.")
            response.isServerError -> throw InternalServerErrorException(prefix + "An internal server error occurred.")
            else -> throw ResponseException(prefix + "An unexpected error occurred. [${response.body}]")
        }
    }
}

/**
 * Suspend extension that allows suspend [Call] inside coroutine.
 *
 * @return Result of request or throw exception
 */
suspend fun Call.await(): Response {
    return suspendCancellableCoroutine { continuation ->
        enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                continuation.resume(response)
            }

            override fun onFailure(call: Call, e: IOException) {
                // Don't bother with resuming the continuation if it is already cancelled.
                if (continuation.isCancelled) return
                continuation.resumeWithException(e)
            }
        })

        continuation.invokeOnCancellation {
            try {
                cancel()
            } catch (ex: Throwable) {
                println(ex.printStackTrace())
            }
        }
    }
}