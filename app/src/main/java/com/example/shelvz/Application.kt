package com.example.shelvz

import android.app.Application
import androidx.activity.compose.setContent
import com.example.shelvz.data.AppDatabase
//import com.example.shelvz.data.DatabaseProvider
import com.example.shelvz.data.repository.UserRepository
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ShelvzApp : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}

