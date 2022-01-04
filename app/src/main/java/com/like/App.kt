package com.like

import android.app.Application
import com.like.daggerModules.AppComponent
import com.like.daggerModules.DaggerAppComponent
import com.like.daggerModules.MainActivityComponent
import com.like.daggerModules.MainActivityModules

class App: Application() {
    // компонент приложения
    val appComponent: AppComponent = DaggerAppComponent.create()
    // компонент MainActivity
    var mainActivityComponent: MainActivityComponent? = appComponent.mainActivityComponent(MainActivityModules())
}