package com.like.audioPlay

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.like.MainActivity
import com.like.R
import com.like.databinding.AudioPlayFragmentBinding

class AudioPlay : Fragment() {
    private val model by lazy { ViewModelProvider(activity as MainActivity)[AudioPlayModel::class.java] }
    lateinit var binding: AudioPlayFragmentBinding
    private var fragmentVisibility: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentVisibility = container?.visibility == View.VISIBLE
        binding = DataBindingUtil.inflate(inflater,
            R.layout.audio_play_fragment, container, false)
        if (fragmentVisibility) {
            model.onCreateView(this)
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if (fragmentVisibility) {
            model.onStart(this)
        }
    }
}