package com.like.daggerModules

import com.like.MainActivity
import com.like.audioPlay.AudioPlay
import com.like.audioPlayFullscreen.AudioPlayFullscreen
import dagger.Component
import dagger.Subcomponent
import javax.inject.Scope
import javax.inject.Singleton

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class MainActivityScope

@Component(modules = [AppModules::class])
interface AppComponent {
    fun mainActivityComponent(mainActivityModules: MainActivityModules): MainActivityComponent
    fun inject(activity: MainActivity)
}

@Singleton
@MainActivityScope
@Subcomponent(modules = [MainActivityModules::class])
interface MainActivityComponent {
    fun inject(audioPlay: AudioPlay)
    fun inject(audioPlayFullscreen: AudioPlayFullscreen)
}