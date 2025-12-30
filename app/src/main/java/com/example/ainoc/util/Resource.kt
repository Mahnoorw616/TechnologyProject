package com.example.ainoc.util

// This is a special container that wraps around our data to tell us its status.
// It helps the screen know if it should show a loading spinner, the actual data, or an error message.
sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    // Used when the data was fetched successfully.
    class Success<T>(data: T) : Resource<T>(data)

    // Used when something went wrong (like no internet), and includes an error message.
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)

    // Used while the app is busy fetching the data (shows a spinner).
    class Loading<T>(data: T? = null) : Resource<T>(data)

    // Used when nothing has happened yet (initial state).
    class Idle<T> : Resource<T>()
}