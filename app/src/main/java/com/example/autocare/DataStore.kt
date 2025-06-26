package com.example.autocare

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStore(private val context: Context) {

    companion object {
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode_enabled")
        val NOTIFICATIONS_KEY = booleanPreferencesKey("notifications_enabled")
    }

    val darkModeFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DARK_MODE_KEY] ?: false
        }

    val notificationsFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[NOTIFICATIONS_KEY] ?: true
        }


    suspend fun toggleDarkMode(isEnabled: Boolean) {
        context.dataStore.edit { settings ->
            settings[DARK_MODE_KEY] = isEnabled
        }
    }


    suspend fun toggleNotifications(isEnabled: Boolean) {
        context.dataStore.edit { settings ->
            settings[NOTIFICATIONS_KEY] = isEnabled
        }
    }
}