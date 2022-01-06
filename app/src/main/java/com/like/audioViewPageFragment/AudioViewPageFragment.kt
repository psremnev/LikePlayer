package com.like.audioViewPageFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.like.App
import com.like.R
import com.like.databinding.AudioImageScrollItemBinding
import javax.inject.Inject

class AudioViewPageFragment(): Fragment() {
    @Inject lateinit var fragmentModel: AudioViewPageFragmentModel
    lateinit var binding: AudioImageScrollItemBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mainActivityComponent = (activity?.application as App).mainActivityComponent
        mainActivityComponent?.inject(this)

        binding = DataBindingUtil.inflate(inflater, R.layout.audio_image_scroll_item, container, false)
        fragmentModel.onCreateView(this)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        fragmentModel.onDestroy()
    }
}