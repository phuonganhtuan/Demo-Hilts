package com.example.demohilts

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DemoHiltsApp: Application() {

    override fun onCreate() {
        super.onCreate()
    }
}
