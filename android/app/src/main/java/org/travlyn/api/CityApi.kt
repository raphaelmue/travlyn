package org.travlyn.api

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.webkit.URLUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import org.travlyn.api.model.City
import org.travlyn.api.model.Trip
import org.travlyn.infrastructure.*
import org.travlyn.local.Application
import java.io.InputStream

class CityApi(
    application: Application? = null
) : ApiClient(application = application) {

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

    suspend fun getPublicTripsForCity(cityId: Int): Array<Trip> {
        val localVariableQuery: MultiValueMap = mapOf("cityId" to listOf(cityId.toString()))
        val localVariableConfig = RequestConfig(
            RequestMethod.GET,
            "/city/trips", query = localVariableQuery
        )
        val response = request<Array<Trip>>(
            localVariableConfig
        )

        return when (response.responseType) {
            ResponseType.Success -> {
                if ((response as Success<*>).data != null) {
                    (response as Success<*>).data as Array<Trip>
                } else {
                    emptyArray()
                }
            }
            ResponseType.Informational -> TODO()
            ResponseType.Redirection -> TODO()
            ResponseType.ClientError -> {
                if ((response as ClientError<*>).statusCode == 404) {
                    emptyArray()
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