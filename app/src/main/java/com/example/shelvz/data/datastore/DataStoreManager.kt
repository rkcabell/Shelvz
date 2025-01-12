package com.example.shelvz.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class DataStoreManager @Inject constructor(@ApplicationContext private val context: Context) {

    companion object {
        private val Context.dataStore by preferencesDataStore(name = "user_prefs")
    }

    private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    private val LOGGED_IN_USER_ID = stringPreferencesKey("logged_in_user_id")

    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_LOGGED_IN] ?: false
        }

    val loggedInUserId: Flow<UUID?> = context.dataStore.data
        .map { preferences ->
            preferences[LOGGED_IN_USER_ID]?.let { UUID.fromString(it) }
        }

    suspend fun setUserLoggedIn(isLoggedIn: Boolean, userId: UUID? = null) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = isLoggedIn
            if (isLoggedIn) {
                userId?.let { preferences[LOGGED_IN_USER_ID] = it.toString()}
            } else {
                preferences.remove(LOGGED_IN_USER_ID)
            }
        }
    }
}