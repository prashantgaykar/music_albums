package io.prashant.topalbums.util

sealed class ApiResult<out T : Any?> {
    data class Success<out T : Any?>(val value: T) : ApiResult<T>()
    data class Failure(val exception: Exception) : ApiResult<Nothing>()
    data class Loading(val isLoading: Boolean, val msg: String = "") : ApiResult<Nothing>()
    companion object {
        fun loading(flag: Boolean, msg: String = ""): ApiResult<Nothing> = Loading(flag, msg)
        fun <T : Any?> success(value: T): ApiResult<T> = Success(value)
        fun failure(exception: Exception): ApiResult<Nothing> = Failure(exception)
    }
}