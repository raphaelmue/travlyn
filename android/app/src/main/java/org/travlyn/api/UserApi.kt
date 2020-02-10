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

import org.travlyn.api.model.Trip
import org.travlyn.api.model.User
import org.travlyn.infrastructure.*
import org.travlyn.local.Application

class UserApi(
    basePath: String = "http://travlyn.raphael-muesseler.de/travlyn/travlyn/1.0.0/",
    application: Application
) :
    ApiClient(basePath, application) {

    /**
     * Get all Trips of user
     *
     * @param userId ID of the user whose trips are to be returned
     * @return kotlin.Array<Trip>
     */
    @Suppress("UNCHECKED_CAST")
    suspend fun getTripsByUserId(userId: Long): Array<Trip> {

        val localVariableConfig = RequestConfig(
            RequestMethod.GET,
            "/user/{userId}/trips".replace("{" + "userId" + "}", "$userId")
        )
        val response = request<Array<Trip>>(
            localVariableConfig
        )

        return when (response.responseType) {
            ResponseType.Success -> (response as Success<*>).data as Array<Trip>
            ResponseType.Informational -> TODO()
            ResponseType.Redirection -> TODO()
            ResponseType.ClientError -> throw ClientException(
                (response as ClientError<*>).body as? String ?: "Client error"
            )
            ResponseType.ServerError -> {
                throw ServerException(
                    (response as ServerError<*>).message ?: "Server error"
                )
            }
        }
    }

    /**
     * Logs user into the system
     *
     * @param email The email for login
     * @param password The password for login in clear text
     * @return User
     */
    @Suppress("UNCHECKED_CAST")
    suspend fun loginUser(email: String, password: String): User? {
        val localVariableQuery: MultiValueMap =
            mapOf("email" to listOf(email), "password" to listOf(password))
        val localVariableConfig = RequestConfig(
            RequestMethod.GET,
            "/user", query = localVariableQuery
        )
        val response = request<User>(
            localVariableConfig
        )

        println(response)
        return when (response.responseType) {
            ResponseType.Success -> {
                if ((response as Success<*>).data != null) {
                    (response as Success<*>).data as User
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

    /**
     * Log out current logged in user session
     *
     * @param user The user to logout
     * @return void
     */
    suspend fun logoutUser(user: User) {
        val localVariableQuery: MultiValueMap = user.toMap().toQueryParameters()
        val localVariableConfig = RequestConfig(
            RequestMethod.DELETE,
            "/user", query = localVariableQuery
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
                (response as ServerError<*>).message
                    ?: "Server error (code: ${response.statusCode})"
            )
        }
    }

    /**
     * Create a new User
     *
     * @param email The email for registration
     * @param name The name for registration
     * @param password The password for registration in clear text
     * @return User
     */
    @Suppress("UNCHECKED_CAST")
    suspend fun registerUser(email: String, name: String, password: String): User {
        val localVariableQuery: MultiValueMap = mapOf(
            "email" to listOf(email),
            "Name" to listOf(name),
            "password" to listOf(password)
        )
        val localVariableConfig = RequestConfig(
            RequestMethod.PUT,
            "/user", query = localVariableQuery
        )
        val response = request<User>(
            localVariableConfig
        )

        return when (response.responseType) {
            ResponseType.Success -> (response as Success<*>).data as User
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
     * Update users information
     *
     * @param user Updated user object
     * @return void
     */
    suspend fun updateUser(user: User): Unit {
        val localVariableQuery: MultiValueMap = mapOf("user" to listOf("$user"))
        val localVariableConfig = RequestConfig(
            RequestMethod.POST,
            "/user", query = localVariableQuery
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

}
