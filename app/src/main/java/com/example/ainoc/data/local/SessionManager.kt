package com.example.ainoc.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ainoc.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// This creates a small, efficient storage file on the phone to save simple settings like login tokens.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constants.SESSION_PREFERENCES)

// This class handles all saving and reading of data. Hilt provides it to other parts of the app when needed.
class SessionManager @Inject constructor(context: Context) {

    // A reference to the storage file created above so we can use it inside this class.
    private val dataStore = context.dataStore

    // These are the specific labels (Keys) used to find our data in the storage file.
    companion object {
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val SERVER_URL = stringPreferencesKey("server_url")
        val APP_THEME = stringPreferencesKey("app_theme")
    }

    // Saves the user's login token to storage. It pauses execution (suspend) to write data without freezing the app.
    suspend fun saveAuthToken(token: String) {
        dataStore.edit { preferences ->
            preferences[AUTH_TOKEN] = token
        }
    }

    // specific live stream of the token. If the token changes, any screen watching this gets updated immediately.
    val authToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[AUTH_TOKEN]
    }

    // Saves the server address typed by the user so they don't have to re-enter it later.
    suspend fun saveServerUrl(url: String) {
        dataStore.edit { preferences ->
            preferences[SERVER_URL] = url
        }
    }

    // Provides the saved server URL to the login screen whenever needed.
    val serverUrl: Flow<String?> = dataStore.data.map { preferences ->
        preferences[SERVER_URL]
    }

    // Remembers the user's choice of Light or Dark mode.
    suspend fun saveTheme(theme: String) {
        dataStore.edit { preferences ->
            preferences[APP_THEME] = theme
        }
    }

    // Streams the current theme. If no theme is saved yet (first run), it defaults to "DARK".
    val appTheme: Flow<String> = dataStore.data.map { preferences ->
        preferences[APP_THEME] ?: "DARK"
    }

    // Wipes all saved data from the file. This is used when the user logs out for security.
    suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}