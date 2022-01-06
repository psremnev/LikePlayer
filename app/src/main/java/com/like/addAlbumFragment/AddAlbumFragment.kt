package com.like.addAlbumFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.like.*
import com.like.databinding.AddAlbumFragmentBinding
import javax.inject.Inject

class AddAlbumFragment: DialogFragment() {
    @Inject lateinit var model: AddAlbumFragmentModel
    lateinit var binding: AddAlbumFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mainActivityComponent = (activity?.application as App).mainActivityComponent
        mainActivityComponent?.inject(this)

        binding = DataBindingUtil.inflate(inflater,
            R.layout.add_album_fragment, container, false)
        model.onCreateView(this)
        return binding.root
    }
}