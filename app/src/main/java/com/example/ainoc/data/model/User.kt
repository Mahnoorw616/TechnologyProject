package com.example.ainoc.data.model

data class User(
    val username: String,
    val email: String,
    val role: String = "Administrator",
    val memberSince: String = "Dec 21, 2025"
)