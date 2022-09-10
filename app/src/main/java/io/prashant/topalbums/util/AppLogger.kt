package io.prashant.topalbums.util

import android.util.Log
import io.prashant.topalbums.BuildConfig


object AppLogger {

    private val isShow = BuildConfig.DEBUG

    fun e(tag: String, msg: String, error: Throwable? = null) {
        if (isShow) {
            Log.e(tag, msg, error)
        }
    }
}