package org.travlyn.infrastructure

import android.app.Activity
import android.content.res.Resources

fun Activity.dpToPx(dp: Int): Int {
    return (dp * Resources.getSystem().displayMetrics.density).toInt()
}

fun Activity.pxToDp(px: Int): Int {
    return (px / Resources.getSystem().displayMetrics.density).toInt()
}
