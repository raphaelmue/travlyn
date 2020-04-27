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
import org.travlyn.infrastructure.error.TravlynException
import org.travlyn.local.Application
import java.io.InputStream

class CityApi(
    application: Application
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
        var result: City? = null
        try {
            result = request<City>(
                localVariableConfig
            )
        } catch (exception: TravlynException) {
            application.showErrorDialog(exception)
        }

        return result
    }

    suspend fun getPublicTripsForCity(cityId: Long): Array<Trip> {
        val localVariableQuery: MultiValueMap = mapOf("cityId" to listOf(cityId.toString()))
        val localVariableConfig = RequestConfig(
            RequestMethod.GET,
            "/city/trips", query = localVariableQuery
        )
        return request<Array<Trip>>(
            localVariableConfig
        ) ?: emptyArray()
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