package com.example.shelvz

import android.app.Application
import androidx.activity.compose.setContent
import com.example.shelvz.data.AppDatabase
import com.example.shelvz.data.DatabaseProvider
import com.example.shelvz.data.repository.UserRepository
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ShelvzApp : Application() {

    // Initialize the database and repository
    val database by lazy { DatabaseProvider.getDatabase(this) }
    // Initialize network libraries (like RetroFit)

    override fun onCreate() {
        super.onCreate()
    }
}

