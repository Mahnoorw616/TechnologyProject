package com.example.ainoc.di

import android.content.Context
import com.example.ainoc.data.local.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// This is a setup module for our dependency injection tool (Hilt).
// It tells the app how to create and provide core tools that live as long as the app is running.
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // This function teaches Hilt how to make a SessionManager.
    // Whenever any part of the app asks for "SessionManager", Hilt runs this code to give it one.
    @Singleton
    @Provides
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager {
        return SessionManager(context)
    }
}