package com.github.ymaniz09.cleannotes.business.data.util

import com.github.ymaniz09.cleannotes.business.data.network.ApiResult
import com.github.ymaniz09.cleannotes.business.data.network.NetworkConstants
import com.github.ymaniz09.cleannotes.business.data.network.NetworkErrors
import com.github.ymaniz09.cleannotes.util.cLog
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import retrofit2.HttpException
import java.io.IOException
import java.net.HttpURLConnection

suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher,
    apiCall: suspend () -> T?
): ApiResult<T?> {
    return withContext(dispatcher) {
        try {
            withTimeout(NetworkConstants.NETWORK_TIMEOUT) {
                ApiResult.Success(apiCall.invoke())
            }
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            when (throwable) {
                is TimeoutCancellationException -> {
                    buildTimeoutErrorResult()
                }

                is IOException -> {
                    ApiResult.NetworkError
                }

                is HttpException -> {
                    val code = throwable.code()
                    val errorResponse = convertErrorBody(throwable)
                    cLog(errorResponse)
                    ApiResult.GenericError(
                        code,
                        errorResponse
                    )
                }
                else -> {
                    cLog(NetworkErrors.NETWORK_ERROR_UNKNOWN)
                    buildGenericErrorResult()
                }
            }
        }
    }
}

private fun convertErrorBody(throwable: HttpException): String? {
    return try {
        throwable.response()?.errorBody()?.string()
    } catch (exception: Exception) {
        GenericErrors.ERROR_UNKNOWN
    }
}

fun buildTimeoutErrorResult() =
    ApiResult.GenericError(
        HttpURLConnection.HTTP_CLIENT_TIMEOUT,
        NetworkErrors.NETWORK_ERROR_TIMEOUT
    )

fun buildGenericErrorResult() =
    ApiResult.GenericError(
        null,
        NetworkErrors.NETWORK_ERROR_UNKNOWN
    )