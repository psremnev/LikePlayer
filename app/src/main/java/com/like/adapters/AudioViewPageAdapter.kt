package com.like.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.like.Constants
import com.like.MainActivity
import com.like.audioViewPage.AudioViewPage

class AudioViewPageAdapter(ctx: MainActivity, private val audioData: ArrayList<Constants.Audio>): FragmentStateAdapter(ctx) {

    override fun getItemCount(): Int {
        return audioData.size
    }

    override fun createFragment(position: Int): Fragment {
        return AudioViewPage(position)
    }
}