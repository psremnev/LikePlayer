package com.like

import android.app.Application
import android.content.Context
import com.like.daggerModules.AppComponent
import com.like.daggerModules.DaggerAppComponent
import com.like.daggerModules.MainActivityComponent
import com.like.daggerModules.MainActivityModules

class App: Application() {
    // компонент приложения
    val appComponent: AppComponent = DaggerAppComponent.create()
    // компонент MainActivity
    var mainActivityComponent: MainActivityComponent? = null

    fun createMainActivityComponent(ctx: MainActivity) {
        mainActivityComponent = appComponent.mainActivityComponent(MainActivityModules(ctx))
    }

    fun clearMainActivityComponent() {
        mainActivityComponent = null
    }
}