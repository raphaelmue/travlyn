package org.travlyn.api

import org.travlyn.api.model.City
import org.travlyn.api.model.User
import org.travlyn.infrastructure.*
import org.travlyn.local.Application

class CityApi(
    basePath: String = "http://travlyn.raphael-muesseler.de/travlyn/travlyn/1.0.0/",
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
            ResponseType.ClientError -> throw ClientException(
                (response as ClientError<*>).body as? String ?: "Client error"
            )
            ResponseType.ServerError -> throw ServerException(
                (response as ServerError<*>).message ?: "Server error"
            )
        }
    }
}