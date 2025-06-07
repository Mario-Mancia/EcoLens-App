package com.example.ecolens.data.local.session

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class SessionManager(private val context: Context) {

    companion object {
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
    }

    suspend fun saveLoginSession(email: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_EMAIL_KEY] = email
        }
    }

    suspend fun getLoggedUserEmail(): String? {
        return context.dataStore.data
            .map { prefs -> prefs[USER_EMAIL_KEY] }
            .first()
    }

    suspend fun logout() {
        context.dataStore.edit { prefs ->
            prefs.remove(USER_EMAIL_KEY)
        }
    }
}