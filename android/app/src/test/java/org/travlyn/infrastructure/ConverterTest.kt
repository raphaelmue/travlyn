package org.travlyn.infrastructure

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.travlyn.api.model.*

internal class ConverterTest {

    @Test
    fun testToQueryParameters() {
        val user = Trip(
            id = 1,
            private = false,
            city = City(
                id = 1,
                name = "TestCity"
            ),
            user = User(
                id = 1,
                email = "test@email.com",
                name = "TestUser",
                token = Token(
                    id = 1,
                    token = "6406b2e97a97f64910aca76370ee35a92087806da1aa878e8a9ae0f4dc3949af"
                )
            ),
            stops = arrayOf(
                Stop(
                    id = 1,
                    name = "First POI"
                ),
                Stop(
                    id = 2,
                    name = "Second POI"
                )
            )
        )

        val map = user.toMap()
        val parameters = map.toQueryParameters()

        // 34 is the total number of attributes of the trip including null properties
        Assertions.assertEquals(34, parameters.size)
        Assertions.assertEquals("1", parameters["stops[0].id"]?.get(0))
        parameters["stop[1].name"]?.isNotEmpty()?.let { Assertions.assertTrue(it) }
    }
}