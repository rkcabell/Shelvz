package com.example.shelvz.data

import android.content.Context
import androidx.room.Room

/*
    TO ACCESS EXAMPLE:
    val database = DatabaseProvider.getDatabase(context)
    val userDao = database.userDao()
 */

object DatabaseProvider {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    // Synchronized block to ensure thread safety
    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}

