/**
 * Travlyn API
 * Travlyn is an intelligent travel and city guide that provides interest-based trips in cities and countries. Depending on available time, interests, budget and many other parameters, Travlyn creates personalized routes with additional information about the locations themselves and the sights.
 *
 * OpenAPI spec version: 1.0.0
 * Contact: raphael@muesseler.de
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */
package org.travlyn.api

import android.media.Rating
import org.travlyn.api.model.Stop

import org.travlyn.infrastructure.*

class StopApi(basePath: String = "https://virtserver.swaggerhub.com/travlyn/travlyn/1.0.0") :
    ApiClient(basePath) {

    /**
     * Rate a stop
     *
     * @param stopId ID of the stop that will be rated
     * @param rating Rating to be created
     * @return void
     */
    suspend fun rateStop(stopId: Long, rating: Rating): Unit {
        val localVariableQuery: MultiValueMap = mapOf("rating" to listOf("$rating"))
        val localVariableConfig = RequestConfig(
            RequestMethod.POST,
            "/stop/{stopId}".replace("{" + "stopId" + "}", "$stopId"), query = localVariableQuery
        )
        val response = request<Any?>(
            localVariableConfig
        )

        return when (response.responseType) {
            ResponseType.Success -> Unit
            ResponseType.Informational -> TODO()
            ResponseType.Redirection -> TODO()
            ResponseType.ClientError -> throw ClientException(
                (response as ClientError<*>).body as? String ?: "Client error"
            )
            ResponseType.ServerError -> throw ServerException(
                (response as ServerError<*>).message ?: "Server error"
            )
        }
    }

    /**
     * Get Stop by ID
     *
     * @param stopId ID of the stop that will be returned
     * @return Stop
     */
    @Suppress("UNCHECKED_CAST")
    suspend fun stopStopIdGet(stopId: Long): Stop {

        val localVariableConfig = RequestConfig(
            RequestMethod.GET,
            "/stop/{stopId}".replace("{" + "stopId" + "}", "$stopId")
        )
        val response = request<Stop>(
            localVariableConfig
        )

        return when (response.responseType) {
            ResponseType.Success -> (response as Success<*>).data as Stop
            ResponseType.Informational -> TODO()
            ResponseType.Redirection -> TODO()
            ResponseType.ClientError -> throw ClientException(
                (response as ClientError<*>).body as? String ?: "Client error"
            )
            ResponseType.ServerError -> throw ServerException(
                (response as ServerError<*>).message ?: "Server error"
            )
        }
    }

}
