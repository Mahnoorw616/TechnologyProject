package com.example.ainoc.di

import com.example.ainoc.data.local.SessionManager // Import SessionManager
import com.example.ainoc.data.remote.ApiService
import com.example.ainoc.data.remote.AuthInterceptor
import com.example.ainoc.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Provides the AuthInterceptor.
     * Hilt knows how to provide the SessionManager dependency from AppModule.
     */
    @Singleton
    @Provides
    fun provideAuthInterceptor(sessionManager: SessionManager): AuthInterceptor {
        return AuthInterceptor(sessionManager)
    }

    /**
     * Provides a singleton OkHttpClient instance.
     * This function now works because Hilt knows how to create its AuthInterceptor dependency.
     */
    @Singleton
    @Provides
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Logs network request and response bodies
        }
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)      // Adds the auth token to requests
            .addInterceptor(loggingInterceptor)   // Logs network traffic
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Provides a singleton Retrofit instance.
     */
    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Provides the ApiService interface for making network calls.
     */
    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}