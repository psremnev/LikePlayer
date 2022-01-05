package com.like.adapters

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.like.Constants
import com.like.MainActivity
import com.like.MainActivityModel
import com.like.audioViewPage.AudioViewPage
import com.like.audioViewPage.AudioViewPageModel
import com.like.dataClass.Audio

class AudioViewPageAdapter(ctx: MainActivity, private val audioData: ArrayList<Audio>): FragmentStateAdapter(ctx) {
    val model: AudioViewPageModel by lazy { ViewModelProvider(ctx)[AudioViewPageModel::class.java] }

    override fun getItemCount(): Int {
        return audioData.size
    }

    override fun createFragment(position: Int): Fragment {
        model.createViewPositionObservable.onNext(position)
        return AudioViewPage()
    }
}