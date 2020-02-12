package org.travlyn.local

import android.content.Context
import org.travlyn.R

class Formatter(var context: Context) {

    fun format(throwable: Throwable): String {
        return context.getString(R.string.error_unknown)
    }

}