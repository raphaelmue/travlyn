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
 * City
 *
 * @param id
 * @param name
 * @param image URL to image
 * @param description
 */
data class City(

    val id: Int? = null,
    val name: String? = null,
    /* URL to image */
    val image: String? = null,
    val description: String? = null
)