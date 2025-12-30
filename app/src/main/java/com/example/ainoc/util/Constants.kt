package com.example.ainoc.util

// This file stores fixed values that are used throughout the app.
// It's like a dictionary of unchanging rules, so we don't have to type them again and again.
object Constants {
    // The main web address (URL) of the server where the app gets its data.
    const val BASE_URL = "https://ainoc.yourcompany.com/api/"

    // The name of the file on the phone where we save user settings (like login tokens).
    const val SESSION_PREFERENCES = "session_preferences"
}