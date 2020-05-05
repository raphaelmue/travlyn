package org.travlyn.api.model

data class ExecutionInfo(
    val tripId: Int? = null,
    val steps: Array<Step> = emptyArray(),
    val distance: Double? = null,
    val duration: Double? = null,
    val waypoints: Array<Waypoint> = emptyArray(),
    val stopIds: Array<Int> = emptyArray()
)