package com.like.daggerModules

import com.like.MainActivity
import com.like.audioPlay.AudioPlay
import com.like.audioPlay.AudioPlayModel
import com.like.audioPlayFullscreen.AudioPlayFullscreen
import com.like.audioPlayFullscreen.AudioPlayFullscreenModel
import dagger.Component
import dagger.Subcomponent
import javax.inject.Singleton

@Component(modules = [AppModules::class])
interface AppComponent {
    fun mainActivityComponent(mainActivityModules: MainActivityModules): MainActivityComponent
    fun inject(activity: MainActivity)
}

@Singleton
@Subcomponent(modules = [MainActivityModules::class])
interface MainActivityComponent {
    fun inject(audioPlay: AudioPlayModel)
    fun inject(audioPlayFullscreen: AudioPlayFullscreenModel)
}