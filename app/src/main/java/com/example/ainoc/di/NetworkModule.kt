package com.example.ainoc.di

import com.example.ainoc.data.local.SessionManager
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

// This module teaches the app how to build the complex network tools needed to talk to the server.
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // Teaches Hilt how to create the AuthInterceptor we defined earlier.
    @Singleton
    @Provides
    fun provideAuthInterceptor(sessionManager: SessionManager): AuthInterceptor {
        return AuthInterceptor(sessionManager)
    }

    // Teaches Hilt how to build the HTTP Client (the engine that sends requests).
    // It configures timeouts and adds our security interceptor and a logging tool for debugging.
    @Singleton
    @Provides
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // Teaches Hilt how to build Retrofit (the tool that translates our ApiService interface into real code).
    // It connects the HTTP Client and sets the base URL (like "https://api.ainoc.com").
    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Finally, this provides the usable ApiService object that ViewModels will actually call.
    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}