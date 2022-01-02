package com.like.audioPlayFullscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.like.MainActivity

class AudioPlayFullscreen : DialogFragment() {
    val model: AudioPlayFullscreenModel by lazy { ViewModelProvider(activity as MainActivity)[AudioPlayFullscreenModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return model.onCreateView(this, inflater, container)
    }

    override fun onStart() {
        super.onStart()
        model.onStart()
    }
}