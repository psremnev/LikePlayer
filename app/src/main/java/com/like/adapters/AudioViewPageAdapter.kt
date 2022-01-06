package com.like.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.like.App
import com.like.MainActivity
import com.like.audioViewPageFragment.AudioViewPageFragment
import com.like.audioViewPageFragment.AudioViewPageFragmentModel
import com.like.dataClass.Audio
import javax.inject.Inject

class AudioViewPageAdapter(ctx: MainActivity, private val audioData: ArrayList<Audio>): FragmentStateAdapter(ctx) {
    @Inject lateinit var fragmentModel: AudioViewPageFragmentModel

    init {
        val mainActivityComponent = (ctx.application as App).mainActivityComponent
        mainActivityComponent?.inject(this)
    }

    override fun getItemCount(): Int {
        return audioData.size
    }

    override fun createFragment(position: Int): Fragment {
        fragmentModel.createViewPositionObservable.onNext(position)
        return AudioViewPageFragment()
    }
}