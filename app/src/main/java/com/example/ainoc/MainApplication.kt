package com.example.ainoc

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * The Application class for the AI-NOC app.
 * The @HiltAndroidApp annotation triggers Hilt's code generation, including a base class
 * for your application that serves as the application-level dependency container.
 */
@HiltAndroidApp
class MainApplication : Application() {
    // Application-level logic can be placed here if needed in the future.
    override fun onCreate() {
        super.onCreate()
        // Initialize libraries like Timber for logging, etc.
    }
}