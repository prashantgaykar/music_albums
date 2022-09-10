package io.prashant.topalbums.util

sealed class AppException(open val msg: String) : Exception(msg) {
    class UnknownException(override val msg: String = "") : AppException(msg)
}