package com.like.daggerModules

import android.app.Application

class App: Application() {
    val appComponent = DaggerAppComponent.create()
    val mainActivityModules = appComponent.mainActivityComponent(MainActivityModules())
}