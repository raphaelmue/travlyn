package org.travlyn.api

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.webkit.URLUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import org.travlyn.api.model.City
import org.travlyn.infrastructure.*
import org.travlyn.local.Application
import java.io.InputStream

class CityApi(
    basePath: String = "http://169.254.244.57:3000/travlyn/travlyn/1.0.0/",
    application: Application
) : ApiClient(basePath, application) {

    /**
     * Get City by search term
     *
     * @param query Name of the city that should be searched for
     * @return void
     */
    suspend fun getCity(query: String): City? {
        val localVariableQuery: MultiValueMap = mapOf("query" to listOf(query))
        val localVariableConfig = RequestConfig(
            RequestMethod.GET,
            "/city", query = localVariableQuery
        )
        val response = request<City>(
            localVariableConfig
        )

        return when (response.responseType) {
            ResponseType.Success -> {
                if ((response as Success<*>).data != null) {
                    (response as Success<*>).data as City
                } else {
                    null
                }
            }
            ResponseType.Informational -> TODO()
            ResponseType.Redirection -> TODO()
            ResponseType.ClientError -> {
                if ((response as ClientError<*>).statusCode == 404) {
                    null
                } else {
                    throw ClientException(
                        (response as ClientError<*>).body as? String ?: "Client error"
                    )
                }
            }
            ResponseType.ServerError -> throw ServerException(
                (response as ServerError<*>).message ?: "Server error"
            )
        }
    }

    suspend fun getImage(url: String): Bitmap? {
        if (URLUtil.isValidUrl(url)) {
            val request: Request = Request.Builder().url(url).build()
            val response = client.newCall(request).await()
            return withContext(Dispatchers.IO) {
                val inputStream: InputStream = response.body!!.byteStream()
                BitmapFactory.decodeStream(inputStream)
            }
        }
        return null
    }
}