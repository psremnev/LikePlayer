package com.like.addAlbumDialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.like.*
import com.like.databinding.AddAlbumDialogBinding

class AddAlbumDialog(): DialogFragment() {
    val model: AddAlbumDialogModel by lazy { ViewModelProvider(activity as MainActivity)[AddAlbumDialogModel::class.java] }
    lateinit var binding: AddAlbumDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model.onCreate(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.add_album_dialog, container, false)
        model.onCreateView()
        return binding.root
    }
}