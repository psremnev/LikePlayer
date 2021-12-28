package com.Like

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get

class AudioViewPageAdapter(ctx: MainActivity): FragmentStateAdapter(ctx) {
    private val model: Model by lazy { ViewModelProvider(ctx).get() }

    override fun getItemCount(): Int {
        return model.audioLiveData.value!!.size
    }

    override fun createFragment(position: Int): Fragment {
        return AudioViewPage()
    }
}