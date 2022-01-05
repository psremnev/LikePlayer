package com.like.selectAlbumDialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.like.App
import com.like.R
import com.like.databinding.SelectAlbumFragmentBinding
import javax.inject.Inject

class SelectAlbumDialog : DialogFragment() {

    @Inject lateinit var model: SelectAlbumDialogModel
    lateinit var binding: SelectAlbumFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mainActivityComponent = (activity?.application as App).mainActivityComponent
        mainActivityComponent?.inject(this)

        binding = DataBindingUtil.inflate(inflater, R.layout.select_album_fragment, container, false)
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