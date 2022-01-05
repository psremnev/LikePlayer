package com.like.dagger

import com.like.MainActivity
import com.like.MainActivityModel
import com.like.adapters.AlbumListAdapter
import com.like.adapters.AudioListAdapter
import com.like.adapters.AudioViewPageAdapter
import com.like.addAlbumDialog.AddAlbumDialog
import com.like.addAlbumDialog.AddAlbumDialogModel
import com.like.audioPlay.AudioPlay
import com.like.audioPlay.AudioPlayModel
import com.like.audioPlayFullscreen.AudioPlayFullscreen
import com.like.audioPlayFullscreen.AudioPlayFullscreenModel
import com.like.audioViewPage.AudioViewPage
import com.like.audioViewPage.AudioViewPageModel
import com.like.selectAlbumDialog.SelectAlbumDialog
import com.like.selectAlbumDialog.SelectAlbumDialogModel
import dagger.Component
import dagger.Subcomponent
import javax.inject.Singleton

@Component(modules = [AppModules::class])
interface AppComponent {
    fun mainActivityComponent(mainActivityModules: MainActivityModules): MainActivityComponent
}

@Singleton
@Subcomponent(modules = [MainActivityModules::class])
interface MainActivityComponent {
    fun inject(mainActivity: MainActivity)
    fun inject(mainActivityModel: MainActivityModel)
    fun inject(audioPlay: AudioPlay)
    fun inject(ctx: AudioPlayModel)
    fun inject(audioPlay: AudioPlayFullscreen)
    fun inject(audioPlayFullscreenModel: AudioPlayFullscreenModel)
    fun inject(audioViewPage: AudioViewPage)
    fun inject(audioViewPage: AudioViewPageModel)
    fun inject(addAlbumDialog: AddAlbumDialog)
    fun inject(addAlbumDialogModel: AddAlbumDialogModel)
    fun inject(albumListAdapter: AlbumListAdapter)
    fun inject(audioListAdapter: AudioListAdapter)
    fun inject(audioViewPageAdapter: AudioViewPageAdapter)
    fun inject(selectAlbumDialog: SelectAlbumDialog)
    fun inject(selectAlbumDialogModel: SelectAlbumDialogModel)
}