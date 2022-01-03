package com.like.audioViewPage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.like.MainActivity
import com.like.R
import com.like.databinding.AudioPlayFullscreenImagePageBinding

class AudioViewPage(val position: Int): Fragment() {
    val model: AudioViewPageModel by lazy { ViewModelProvider(activity as MainActivity)[AudioViewPageModel::class.java] }
    lateinit var binding: AudioPlayFullscreenImagePageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.audio_play_fullscreen_image_page, container, false)
        model.onCreateView(this)
        return binding.root
    }
}