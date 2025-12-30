package com.example.ainoc

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// This is the starting point of the entire application process.
// The @HiltAndroidApp annotation is crucial: it sets up the dependency injection system
// that allows us to use @Inject in our Activities and ViewModels.
@HiltAndroidApp
class MainApplication : Application() {
    // This function runs once when the app is launched.
    // We can use it to initialize global libraries (like logging or analytics) in the future.
    override fun onCreate() {
        super.onCreate()
    }
}