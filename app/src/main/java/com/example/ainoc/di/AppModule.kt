package com.example.ainoc.di

import android.content.Context
import com.example.ainoc.data.local.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // These dependencies will live as long as the application
object AppModule {

    /**
     * Provides a singleton instance of SessionManager.
     * @param context The application context, provided by Hilt.
     * @return A singleton SessionManager instance.
     */
    @Singleton
    @Provides
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager {
        return SessionManager(context)
    }
}