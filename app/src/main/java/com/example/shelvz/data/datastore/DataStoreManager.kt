package com.example.shelvz.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class DataStoreManager @Inject constructor(@ApplicationContext private val context: Context) {

    companion object {
        private val Context.dataStore by preferencesDataStore(name = "user_prefs")
    }

    private val ISLOGGEDIN = booleanPreferencesKey("is_logged_in")
    private val USERNAMEKEY = stringPreferencesKey("username")
    private val PASSWORDKEY = stringPreferencesKey("password")

    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[ISLOGGEDIN] ?: false }


    val username: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[USERNAMEKEY] }

    val password: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[PASSWORDKEY] }

    suspend fun saveCredentials(username: String, password: String) {
        context.dataStore.edit { preferences ->
            preferences[USERNAMEKEY] = username
            preferences[PASSWORDKEY] = password
        }
    }

    suspend fun setUserLoginStatus(isLoggedIn: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ISLOGGEDIN] = isLoggedIn
        }
    }

    suspend fun logout() {
        context.dataStore.edit { preferences ->
            preferences.remove(ISLOGGEDIN)
            preferences.remove(USERNAMEKEY)
            preferences.remove(PASSWORDKEY)
        }
    }
}
