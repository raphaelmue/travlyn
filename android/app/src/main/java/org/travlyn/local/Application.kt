package org.travlyn.local

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import org.travlyn.R

interface Application {

    val tag: String
        get() = "Application"

    val formatter: Formatter
        get() = Formatter(getContext())

    /**
     * Shows an error dialog with a given message.
     *
     * @param throwable throwable
     * @return void
     */
    fun showErrorDialog(throwable: Throwable) {
        Log.e(tag, throwable.message, throwable)
        AlertDialog.Builder(getContext())
            .setTitle("Travlyn")
            .setMessage(formatter.format(throwable))
            .setPositiveButton(R.string.ok, null)
            .setIcon(R.drawable.ic_error)
            .show()
    }

    /**
     * Returns the context of this application.
     *
     * @return context object
     */
    fun getContext(): Context
}