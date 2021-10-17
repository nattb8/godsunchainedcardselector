package com.nattb8.godsunchainedcardselector.data.model

sealed class NetworkResponse<T> {
    data class Success<T>(val value: T) : NetworkResponse<T>()
    data class Error<T>(val throwable: Throwable) : NetworkResponse<T>()
}

suspend fun <T> apiCall(call: suspend () -> T): NetworkResponse<T> = try {
    NetworkResponse.Success(call())
} catch (exception: Exception) {
    NetworkResponse.Error(exception)
}