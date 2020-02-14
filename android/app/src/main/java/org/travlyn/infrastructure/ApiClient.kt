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
import org.travlyn.local.Application
import java.io.File
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

open class ApiClient(val baseUrl: String, open val application: Application) {
    companion object {
        protected const val ContentType = "Content-Type"
        protected const val Accept = "Accept"
        protected const val JsonMediaType = "application/json"
        protected const val FormDataMediaType = "multipart/form-data"
        protected const val XmlMediaType = "application/xml"

        @JvmStatic
        val client: OkHttpClient = OkHttpClient()

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
    ): ApiInfrastructureResponse<T?> {
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

        val realRequest = request.build()
        val response = client.newCall(realRequest).await()

        val content: String? = withContext(Dispatchers.IO) {
            response.body?.string()
        }

        when {
            response.isRedirect -> return Redirection(
                response.code,
                response.headers.toMultimap()
            )
            response.isInformational -> return Informational(
                response.message,
                response.code,
                response.headers.toMultimap()
            )
            response.isSuccessful -> return Success(
                responseBody(content, accept),
                response.code,
                response.headers.toMultimap()
            )
            response.isClientError -> return ClientError(
                content,
                response.code,
                response.headers.toMultimap()
            )
            else -> return ServerError(
                null,
                content,
                response.code,
                response.headers.toMultimap()
            )
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