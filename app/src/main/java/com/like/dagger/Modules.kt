package com.like.dagger

import androidx.lifecycle.ViewModelProvider
import com.like.DataModel
import com.like.MainActivity
import com.like.MainActivityModel
import com.like.addAlbumDialog.AddAlbumDialogModel
import com.like.audioPlay.AudioPlayModel
import com.like.audioPlayFullscreen.AudioPlayFullscreenModel
import com.like.audioViewPage.AudioViewPageModel
import com.like.selectAlbumDialog.SelectAlbumDialogModel
import dagger.Module
import dagger.Provides

@Module
class AppModules {
}

@Module
class MainActivityModules(val ctx: MainActivity) {

    @Provides
    fun providesDataModel(): DataModel {
        return DataModel(ctx)
    }

    @Provides
    fun providesMainActivityModel(): MainActivityModel {
        return ViewModelProvider(ctx)[MainActivityModel::class.java]
    }

    @Provides
    fun providesAudioPlayModel(): AudioPlayModel {
        return ViewModelProvider(ctx)[AudioPlayModel::class.java]
    }

    @Provides
    fun providesAudioPlayFullscreenModel(): AudioPlayFullscreenModel {
        return ViewModelProvider(ctx)[AudioPlayFullscreenModel::class.java]
    }

    @Provides
    fun providesAddAlbumDialogModel(): AddAlbumDialogModel {
        return ViewModelProvider(ctx)[AddAlbumDialogModel::class.java]
    }

    @Provides
    fun providesAudioViewPageModel(): AudioViewPageModel {
        return ViewModelProvider(ctx)[AudioViewPageModel::class.java]
    }

    @Provides
    fun providesSelectAlbumDialogModel(): SelectAlbumDialogModel {
        return ViewModelProvider(ctx)[SelectAlbumDialogModel::class.java]
    }
}