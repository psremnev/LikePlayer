package com.like.audioPlayFullscreenFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.like.App
import com.like.R
import com.like.databinding.AudioPlayFullscreenFragmentBinding
import javax.inject.Inject

class AudioPlayFullscreenFragment : DialogFragment() {
    @Inject lateinit var fragmentModel: AudioPlayFullscreenFragmentModel
    lateinit var binding: AudioPlayFullscreenFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mainActivityComponent = (activity?.application as App).mainActivityComponent
        mainActivityComponent?.inject(this)

        binding = DataBindingUtil.inflate(inflater,
            R.layout.audio_play_fullscreen_fragment, container, false)
        fragmentModel.onCreateView(this)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        fragmentModel.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
        fragmentModel.onDestroy()
    }
}