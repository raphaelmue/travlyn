package org.travlyn.api.model

data class Step(
    val type: Int? = null,
    val instruction: String? = null,
    val waypoints: Array<Int> = emptyArray()
)