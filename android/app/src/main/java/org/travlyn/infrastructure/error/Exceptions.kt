package org.travlyn.infrastructure.error

import android.content.Context
import org.travlyn.R

abstract class TravlynException(message: String) : RuntimeException(message) {
    open fun format(context: Context): String {
        return context.getString(R.string.error_unknown)
    }
}

class ServerNotAvailableException(message: String) : TravlynException(message) {
    override fun format(context: Context): String {
        return context.getString(R.string.error_server_not_available)
    }
}

open class ResponseException(message: String) : TravlynException(message)

class UnauthorizedException(message: String) : ResponseException(message) {
    override fun format(context: Context): String {
        return context.getString(R.string.error_not_signed_in)
    }
}

class NotFoundException(message: String) : TravlynException(message) {
    override fun format(context: Context): String {
        return context.getString(R.string.error_not_found)
    }
}

class InternalServerErrorException(message: String) : ResponseException(message) {
}