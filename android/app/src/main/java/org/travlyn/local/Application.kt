package org.travlyn.local

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.travlyn.R
import org.travlyn.infrastructure.error.TravlynException

interface Application {

    val tag: String
        get() = "Application"

    /**
     * Shows an error dialog with a given message.
     *
     * @param throwable throwable
     * @return void
     */
    suspend fun showErrorDialog(throwable: TravlynException) = withContext(Dispatchers.Main) {
        Log.e(tag, throwable.message, throwable)
        AlertDialog.Builder(getContext())
            .setTitle("Travlyn")
            .setMessage(throwable.format(getContext()))
            .setPositiveButton(R.string.ok, null)
            .setIcon(R.drawable.ic_error)
            .show()
    }!!

    /**
     * Returns the context of this application.
     *
     * @return context object
     */
    fun getContext(): Context
}