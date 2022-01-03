package com.like

import android.app.Application
import com.like.daggerModules.DaggerAppComponent
import com.like.daggerModules.MainActivityModules

class App: Application() {
    val appComponent = DaggerAppComponent.create()
    val mainActivityModules = appComponent.mainActivityComponent(MainActivityModules())
}