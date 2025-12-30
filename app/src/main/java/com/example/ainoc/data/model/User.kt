package com.example.ainoc.data.model

// This holds the profile details of the person currently using the app.
// We use this to display their name, email, and role on the Profile and Settings screens.
data class User(
    val username: String,
    val email: String,
    val role: String = "Administrator",
    val memberSince: String = "Dec 21, 2025"
)