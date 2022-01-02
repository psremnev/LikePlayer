package com.like.audioPlay

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.like.MainActivity

class AudioPlay : Fragment() {
    private val model by lazy { ViewModelProvider(activity as MainActivity)[AudioPlayModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return model.onCreateView(inflater, container)
    }

    override fun onStart() {
        super.onStart()
        model.onStart(this)
    }
}