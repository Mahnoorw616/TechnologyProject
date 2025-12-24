package com.example.ainoc.data.remote

import com.example.ainoc.data.local.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * An OkHttp Interceptor that adds an Authorization header with a JWT Bearer token.
 * The token is retrieved synchronously from the SessionManager.
 *
 * @param sessionManager The manager responsible for providing the auth token.
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // NOTE: runBlocking is generally discouraged, but it is an accepted and common
        // practice within the synchronous execution model of OkHttp Interceptors.
        val token = runBlocking {
            sessionManager.authToken.first()
        }

        // If a token exists, add it to the request header.
        token?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        return chain.proceed(requestBuilder.build())
    }
}