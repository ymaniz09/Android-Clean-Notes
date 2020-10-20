package com.github.ymaniz09.cleannotes.business.data.cache

sealed class CacheResult<out T> {

    data class Success<out T>(val value: T) : CacheResult<T>()

    data class GenericError(
        val errorMessage: String? = null
    ) : CacheResult<Nothing>()
}