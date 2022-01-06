package com.like.dagger

import com.like.MainActivity
import com.like.MainActivityModel
import com.like.adapters.AlbumListAdapter
import com.like.adapters.AudioListAdapter
import com.like.adapters.AudioViewPageAdapter
import com.like.addAlbumFragment.AddAlbumFragment
import com.like.addAlbumFragment.AddAlbumFragmentModel
import com.like.audioPlayFragment.AudioPlayFragment
import com.like.audioPlayFragment.AudioPlayFragmentModel
import com.like.audioPlayFullscreenFragment.AudioPlayFullscreenFragment
import com.like.audioPlayFullscreenFragment.AudioPlayFullscreenFragmentModel
import com.like.audioViewPageFragment.AudioViewPageFragment
import com.like.audioViewPageFragment.AudioViewPageFragmentModel
import com.like.selectAlbumFragment.SelectAlbumFragment
import com.like.selectAlbumFragment.SelectAlbumFragmentModel
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
    fun inject(audioPlayFragment: AudioPlayFragment)
    fun inject(ctx: AudioPlayFragmentModel)
    fun inject(audioPlayFragment: AudioPlayFullscreenFragment)
    fun inject(audioPlayFullscreenFragmentModel: AudioPlayFullscreenFragmentModel)
    fun inject(audioViewPageFragment: AudioViewPageFragment)
    fun inject(audioViewPageFragment: AudioViewPageFragmentModel)
    fun inject(addAlbumFragment: AddAlbumFragment)
    fun inject(addAlbumFragmentModel: AddAlbumFragmentModel)
    fun inject(albumListAdapter: AlbumListAdapter)
    fun inject(audioListAdapter: AudioListAdapter)
    fun inject(audioViewPageAdapter: AudioViewPageAdapter)
    fun inject(selectAlbumFragment: SelectAlbumFragment)
    fun inject(selectAlbumFragmentModel: SelectAlbumFragmentModel)
}