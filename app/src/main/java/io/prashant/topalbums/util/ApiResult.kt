package io.prashant.topalbums.util

sealed class ApiResult<out T : Any?> {
    data class Success<out T : Any?>(val value: T) : ApiResult<T>()
    data class Failure(val exception: Exception) : ApiResult<Nothing>()
    data class Loading(val isLoading: Boolean) : ApiResult<Nothing>()
    companion object {
        fun loading(flag: Boolean): ApiResult<Nothing> = Loading(flag)
        fun <T : Any?> success(value: T): ApiResult<T> = Success(value)
        fun failure(exception: Exception): ApiResult<Nothing> = Failure(exception)
    }
}