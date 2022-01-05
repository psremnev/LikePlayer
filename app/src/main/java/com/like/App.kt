package com.like

import android.app.Application
import com.like.dagger.AppComponent
import com.like.dagger.DaggerAppComponent
import com.like.dagger.MainActivityComponent
import com.like.dagger.MainActivityModules

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