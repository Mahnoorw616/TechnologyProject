package com.example.ainoc.util

/**
 * A generic sealed class that contains data and status about loading this data.
 * It's used to represent the state of network operations in the ViewModels.
 */
sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Idle<T> : Resource<T>()
}