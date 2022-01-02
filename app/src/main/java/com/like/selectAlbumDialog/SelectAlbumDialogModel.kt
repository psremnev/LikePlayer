package com.like.selectAlbumDialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.databinding.DataBindingUtil
import androidx.databinding.InverseBindingListener
import androidx.databinding.adapters.AdapterViewBindingAdapter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.like.Constants
import com.like.MainActivity
import com.like.MainActivityModel
import com.like.R
import com.like.databinding.AudioPlayBinding
import com.like.databinding.SelectAlbumDialogBinding

class SelectAlbumDialogModel: ViewModel() {
    lateinit var ctx: SelectAlbumDialog
    val model: MainActivityModel by lazy { ViewModelProvider(ctx.activity as MainActivity)[MainActivityModel::class.java] }
    var audioPosition: Int? = null
    lateinit var binding: SelectAlbumDialogBinding
    lateinit var adapter: ArrayAdapter<Any>
    var choiceMode: Int =  ListView.CHOICE_MODE_SINGLE;

    fun onCreateView(ctx:SelectAlbumDialog, inflater: LayoutInflater, container: ViewGroup?): View {
        this.ctx = ctx
        binding = DataBindingUtil.inflate(inflater, R.layout.select_album_dialog, container, false)
        binding.model = this
        return binding.root
    }

    fun onCreate(ctx: SelectAlbumDialog) {
        audioPosition = ctx.arguments?.getInt("audioPosition")
    }

    fun onStart() {
        binding.selectAlbumList.setOnItemClickListener { parent, view, position, id -> onItemClick(
            parent, view, position, id)  }
        val albumNames = ArrayList<Any>()
        for (album in model.albumData) {
            albumNames.add(album.name)
        }
        binding.selectAlbumList.adapter = ArrayAdapter(ctx.requireContext(), android.R.layout.simple_list_item_single_choice,
            albumNames
        )
        binding.selectAlbumList.adapter
        binding.selectAlbumList.setSelection(model.selectedAlbum - 1)
    }

    private fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val albumItem = model.albumData[position]
        albumItem.audioCount = albumItem.audioCount.plus(1)
        val audioItem: Constants.Audio = model.playItemData
        audioItem.album = albumItem.id!!
        model.dataHelper.updateAudio(audioItem)

        model.albumDataObservable.onNext(object: Constants.AlbumAction {
            override val action = "updateAll"
            override val data = model.dataHelper.getAllAlbum()
        })

        model.audioDataObservable.onNext(model.dataHelper.getAllAudioByAlbumId(model.selectedAlbum))
    }
}