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
package org.travlyn.api.model

/**
 * Stop
 *
 * @param id
 * @param longitude
 * @param latitude
 * @param name
 * @param description Additional information about stop
 * @param image URL to image
 * @param pricing Average pricing for one person in USD
 * @param timeEffort Time effort in hours
 * @param averageRating Average percentage rating by user
 * @param ratings
 * @param category
 */
data class Stop(

    val id: Int? = null,
    val longitude: Double? = null,
    val latitude: Double? = null,
    val name: String? = null,
    /* Additional information about stop */
    val description: String? = null,
    /* URL to image */
    val image: String? = null,
    /* Average pricing for one person in USD */
    val pricing: Double? = null,
    /* Time effort in hours */
    val timeEffort: Double? = null,
    /* Average percentage rating by user */
    val averageRating: Double? = null,
    val ratings: Array<Rating>? = null,
    val category: Category? = null
)