package com.like.dagger

import androidx.lifecycle.ViewModelProvider
import com.like.DataModel
import com.like.MainActivity
import com.like.MainActivityModel
import com.like.addAlbumFragment.AddAlbumFragmentModel
import com.like.audioPlayFragment.AudioPlayFragmentModel
import com.like.audioPlayFullscreenFragment.AudioPlayFullscreenFragmentModel
import com.like.audioViewPageFragment.AudioViewPageFragmentModel
import com.like.selectAlbumFragment.SelectAlbumFragmentModel
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
    fun providesAudioPlayModel(): AudioPlayFragmentModel {
        return ViewModelProvider(ctx)[AudioPlayFragmentModel::class.java]
    }

    @Provides
    fun providesAudioPlayFullscreenModel(): AudioPlayFullscreenFragmentModel {
        return ViewModelProvider(ctx)[AudioPlayFullscreenFragmentModel::class.java]
    }

    @Provides
    fun providesAddAlbumDialogModel(): AddAlbumFragmentModel {
        return ViewModelProvider(ctx)[AddAlbumFragmentModel::class.java]
    }

    @Provides
    fun providesAudioViewPageModel(): AudioViewPageFragmentModel {
        return ViewModelProvider(ctx)[AudioViewPageFragmentModel::class.java]
    }

    @Provides
    fun providesSelectAlbumDialogModel(): SelectAlbumFragmentModel {
        return ViewModelProvider(ctx)[SelectAlbumFragmentModel::class.java]
    }
}