package com.like.daggerModules

import android.media.MediaPlayer
import dagger.Module
import dagger.Provides
import javax.inject.Scope
import javax.inject.Singleton

@Module
class AppModules {
}

@Module
class MainActivityModules {

    @Singleton
    @Provides
    fun providesMediaPlayer(): MediaPlayer {
        return MediaPlayer()
    }
}