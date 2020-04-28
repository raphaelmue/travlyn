package org.travlyn.ui.navigation

import android.app.Activity
import android.util.DisplayMetrics
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.TileSystemWebMercator
import org.osmdroid.views.MapView


fun MapView.focusBoundingBox(boundingBox: BoundingBox, animate: Boolean = true, borderSize: Int = 0) {
    if (animate) {
        controller.zoomTo(nextZoom(boundingBox, borderSize))
        controller.animateTo(boundingBox.centerWithDateLine)
    } else {
        controller.setZoom(nextZoom(boundingBox, borderSize))
        controller.animateTo(boundingBox.centerWithDateLine)
    }
}

fun MapView.nextZoom(boundingBox: BoundingBox, borderSize: Int = 0): Double {
    val displayMetrics = DisplayMetrics()
    (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
    val height = displayMetrics.heightPixels
    val width = displayMetrics.widthPixels

    var nextZoom = TileSystemWebMercator().getBoundingBoxZoom(
        boundingBox,
        width - 2 * borderSize,
        height - 2 * borderSize
    )
    if (nextZoom == Double.MIN_VALUE || nextZoom > maxZoomLevel) {
        nextZoom = maxZoomLevel
    }
    return maxZoomLevel.coerceAtMost(nextZoom.coerceAtLeast(minZoomLevel))
}