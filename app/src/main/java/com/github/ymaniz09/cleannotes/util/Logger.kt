package com.github.ymaniz09.cleannotes.util

import android.util.Log
import com.github.ymaniz09.cleannotes.util.Constants.DEBUG
import com.github.ymaniz09.cleannotes.util.Constants.TAG
import com.google.firebase.crashlytics.FirebaseCrashlytics

var isUnitTest = false

fun printLogD(className: String?, message: String) {
    if (DEBUG && !isUnitTest) {
        Log.d(TAG, "$className: $message")
    } else if (DEBUG && isUnitTest) {
        println("$className: $message")
    }
}

fun cLog(msg: String?) {
    msg?.let {
        if (!DEBUG) {
            FirebaseCrashlytics.getInstance().log(it)
        }
    }
}