package com.example.ainoc.data.remote

import com.example.ainoc.data.local.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

// This acts like a security guard for every internet request the app makes.
// It automatically stamps our "VIP Pass" (Auth Token) onto every message sent to the server so the server knows who we are.
@Singleton
class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {

    // This function intercepts the outgoing message, adds the token stamp, and then lets it proceed.
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // We pause briefly to grab the saved token from our secure storage.
        val token = runBlocking {
            sessionManager.authToken.first()
        }

        // If we found a token, we attach it to the header of the request.
        token?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        // Send the modified request on its way.
        return chain.proceed(requestBuilder.build())
    }
}