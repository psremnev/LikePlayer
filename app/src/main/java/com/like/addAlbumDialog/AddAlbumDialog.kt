package com.like.addAlbumDialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.like.*

class AddAlbumDialog(): DialogFragment() {
    val model: AddAlbumDialogModel by lazy { ViewModelProvider(activity as MainActivity)[AddAlbumDialogModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model.onCreate(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return model.onCreateView(inflater, container)
    }
}