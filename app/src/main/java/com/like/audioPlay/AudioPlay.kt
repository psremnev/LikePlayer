package com.like.audioPlay

import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.like.MainActivity
import com.like.R
import com.like.daggerModules.App
import com.like.daggerModules.MainActivityScope
import com.like.databinding.AudioPlayBinding
import javax.inject.Inject

@MainActivityScope
class AudioPlay : Fragment() {
    private val model by lazy { ViewModelProvider(activity as MainActivity)[AudioPlayModel::class.java] }
    lateinit var binding: AudioPlayBinding
    @Inject lateinit var mediaPlayer: MediaPlayer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mainActivityModules = (activity?.application as App).mainActivityModules
        mainActivityModules.inject(this)

        binding = DataBindingUtil.inflate(inflater,
            R.layout.audio_play, container, false)
        model.onCreateView(this)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        model.onStart(this)
    }
}