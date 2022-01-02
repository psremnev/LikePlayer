package com.like.selectAlbumDialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.like.MainActivity

class SelectAlbumDialog : DialogFragment() {

    private val model: SelectAlbumDialogModel by lazy { ViewModelProvider(activity as MainActivity)[SelectAlbumDialogModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return model.onCreateView(this, inflater, container)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model.onCreate(this)
    }

    override fun onStart() {
        super.onStart()
        model.onStart()
    }
}