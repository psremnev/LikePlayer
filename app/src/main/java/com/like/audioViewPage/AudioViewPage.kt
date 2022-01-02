package com.like.audioViewPage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.like.MainActivity

class AudioViewPage(): Fragment() {
    val model: AudioViewPageModel by lazy { ViewModelProvider(activity as MainActivity)[AudioViewPageModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return model.onCreateView(this, inflater, container)
    }
}