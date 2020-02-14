package org.travlyn.local

import android.content.Context

interface Application {
    /**
     * Shows an error dialog with a given message.
     *
     * @param throwable throwable
     * @return void
     */
    fun showErrorDialog(throwable: Throwable)

    /**
     * Returns the context of this application.
     *
     * @return context object
     */
    fun getContext(): Context

    /**
     * Returns the formatter of this application
     *
     * @return formatter object
     */
    fun getFormatter(): Formatter
}