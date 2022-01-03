package com.like.audioPlayFullscreen

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.like.MainActivity
import com.like.R
import com.like.App
import com.like.daggerModules.MainActivityScope
import com.like.databinding.AudioPlayFullscreenBinding
import javax.inject.Inject

@MainActivityScope
class AudioPlayFullscreen : DialogFragment() {
    val model: AudioPlayFullscreenModel by lazy { ViewModelProvider(activity as MainActivity)[AudioPlayFullscreenModel::class.java] }
    lateinit var binding: AudioPlayFullscreenBinding
    @Inject lateinit var mediaPlayer: MediaPlayer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mainActivityModules = (activity?.application as App).mainActivityModules
        mainActivityModules.inject(this)

        binding = DataBindingUtil.inflate(inflater,
            R.layout.audio_play_fullscreen, container, false)
        model.onCreateView(this)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        model.onStart()
    }
}