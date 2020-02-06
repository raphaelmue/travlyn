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
                    token = "asjkdlföa sjkaölsdfköasdfkaö"
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

        val map = user.serializeToMap()
        val parameters = map.toQueryParameters()
        Assertions.assertEquals(13, parameters.size)
        Assertions.assertEquals("1", parameters["stops[0].id"])
        parameters["stop[1].name"]?.isNotEmpty()?.let { Assertions.assertTrue(it) }
    }
}