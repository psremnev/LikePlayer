package com.like.audioViewPage

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

class AudioViewPage(): Fragment() {
    @Inject lateinit var model: AudioViewPageModel
    lateinit var binding: AudioImageScrollItemBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mainActivityComponent = (activity?.application as App).mainActivityComponent
        mainActivityComponent?.inject(this)

        binding = DataBindingUtil.inflate(inflater, R.layout.audio_image_scroll_item, container, false)
        model.onCreateView(this)
        return binding.root
    }
}