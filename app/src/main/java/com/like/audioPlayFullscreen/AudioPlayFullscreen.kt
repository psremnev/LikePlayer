package com.like.audioPlayFullscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.like.MainActivity
import com.like.R
import com.like.databinding.AudioPlayFullscreenFragmentBinding

class AudioPlayFullscreen : DialogFragment() {
    val model: AudioPlayFullscreenModel by lazy { ViewModelProvider(activity as MainActivity)[AudioPlayFullscreenModel::class.java] }
    lateinit var binding: AudioPlayFullscreenFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.audio_play_fullscreen_fragment, container, false)
        model.onCreateView(this)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        model.onStart()
    }
}