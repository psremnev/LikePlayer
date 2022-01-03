package com.like.selectAlbumDialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.like.MainActivity
import com.like.R
import com.like.databinding.SelectAlbumDialogBinding

class SelectAlbumDialog : DialogFragment() {

    private val model: SelectAlbumDialogModel by lazy { ViewModelProvider(activity as MainActivity)[SelectAlbumDialogModel::class.java] }
    lateinit var binding: SelectAlbumDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.select_album_dialog, container, false)
        model.onCreateView(this)
        return binding.root
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