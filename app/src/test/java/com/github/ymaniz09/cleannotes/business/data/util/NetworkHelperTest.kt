package com.github.ymaniz09.cleannotes.business.data.util

import com.github.ymaniz09.cleannotes.business.data.network.ApiResult
import com.github.ymaniz09.cleannotes.business.data.network.NetworkErrors
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@ExperimentalCoroutinesApi
internal class NetworkHelperTest {
    private val testDispatcher = TestCoroutineDispatcher()

    @Test
    fun `should emit success when lambda returns successfully`() {
        runBlockingTest {
            val lambdaResult = true
            val result = safeApiCall(testDispatcher) { lambdaResult }
            assertEquals(ApiResult.Success(lambdaResult), result)
        }
    }

    @Test
    fun `should emit the result as NetworkError when lambda throws IOException`() {
        runBlockingTest {
            val result = safeApiCall(testDispatcher) { throw IOException() }
            assertEquals(ApiResult.NetworkError, result)
        }
    }

    @Test
    fun `should emit the result as NetworkError when lambda throws TimeoutCancellationException`() {
        runBlocking {
            val result = safeApiCall(Dispatchers.IO) {
                CompletableDeferred<Any>().await()
            }
            assertEquals(ApiResult.GenericError(408, NetworkErrors.NETWORK_ERROR_TIMEOUT), result)
        }
    }

    @Test
    fun `should emit the result as GenericError when lambda throws HttpException`() {
        val errorCode = 422
        val errorMessage = "{\"errors\": [\"Unexpected parameter\"]}"
        val responseBody = ResponseBody.create(MediaType.parse("application/json"), errorMessage)

        runBlockingTest {
            val result = safeApiCall(testDispatcher) {
                throw HttpException(Response.error<Any>(errorCode, responseBody))
            }
            assertEquals(ApiResult.GenericError(errorCode, errorMessage), result)
        }
    }

    @Test
    fun `should emit the result as GenericError when lambda throws unknown exception`() {
        runBlockingTest {
            val result = safeApiCall(testDispatcher) { throw IllegalStateException() }
            assertEquals(buildGenericErrorResult(), result)
        }
    }
}