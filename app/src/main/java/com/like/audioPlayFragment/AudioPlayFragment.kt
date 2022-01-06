package com.like.audioPlayFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.like.App
import com.like.R
import com.like.databinding.AudioPlayFragmentBinding
import javax.inject.Inject

class AudioPlayFragment : Fragment() {
    @Inject lateinit var fragmentModel: AudioPlayFragmentModel
    lateinit var binding: AudioPlayFragmentBinding
    private var fragmentVisibility: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mainActivityComponent = (activity?.application as App).mainActivityComponent
        mainActivityComponent?.inject(this)

        fragmentVisibility = container?.visibility == View.VISIBLE
        binding = DataBindingUtil.inflate(inflater,
            R.layout.audio_play_fragment, container, false)
        if (fragmentVisibility) {
            fragmentModel.onCreateView(this)
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if (fragmentVisibility) {
            fragmentModel.onStart(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fragmentModel.onDestroy()
    }
}