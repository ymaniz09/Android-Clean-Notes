package com.github.ymaniz09.cleannotes.business.data.util

import com.github.ymaniz09.cleannotes.business.data.cache.CacheConstants.CACHE_TIMEOUT
import com.github.ymaniz09.cleannotes.business.data.cache.CacheErrors.CACHE_ERROR_TIMEOUT
import com.github.ymaniz09.cleannotes.business.data.cache.CacheErrors.CACHE_ERROR_UNKNOWN
import com.github.ymaniz09.cleannotes.business.data.cache.CacheResult
import com.github.ymaniz09.cleannotes.util.cLog
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

suspend fun <T> safeCacheCall(
    dispatcher: CoroutineDispatcher,
    cacheCall: suspend () -> T?
): CacheResult<T?> {
    return withContext(dispatcher) {
        try {
            withTimeout(CACHE_TIMEOUT) {
                CacheResult.Success(cacheCall.invoke())
            }
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            when (throwable) {
                is TimeoutCancellationException -> {
                    CacheResult.GenericError(CACHE_ERROR_TIMEOUT)
                }
                else -> {
                    cLog(CACHE_ERROR_UNKNOWN)
                    CacheResult.GenericError(CACHE_ERROR_UNKNOWN)
                }
            }
        }
    }
}