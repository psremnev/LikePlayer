package com.like.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.like.MainActivity
import com.like.MainActivityModel
import com.like.audioViewPage.AudioViewPage

class AudioViewPageAdapter(ctx: MainActivity): FragmentStateAdapter(ctx) {
    private val model: MainActivityModel by lazy { ViewModelProvider(ctx).get() }

    override fun getItemCount(): Int {
        return model.audioData.size
    }

    override fun createFragment(position: Int): Fragment {
        return AudioViewPage()
    }
}