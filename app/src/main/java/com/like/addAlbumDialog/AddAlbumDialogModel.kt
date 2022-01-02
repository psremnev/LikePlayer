package com.like.addAlbumDialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.like.Constants
import com.like.MainActivity
import com.like.MainActivityModel
import com.like.databinding.AddAlbumDialogBinding
import com.like.R

class AddAlbumDialogModel: ViewModel() {

    lateinit var binding: AddAlbumDialogBinding
    private lateinit var ctx: AddAlbumDialog
    var albumPosition: Int? = null
    var albumName: ObservableField<String> = ObservableField<String>("")
    val model: MainActivityModel by lazy { ViewModelProvider(ctx.activity as MainActivity)[MainActivityModel::class.java] }

    fun onCreate(ctx: AddAlbumDialog) {
        this.ctx = ctx
        albumPosition = ctx.arguments?.getInt("position")
        albumName.set(ctx.arguments?.getString("name"))
    }

    fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.add_album_dialog, container, false)
        binding.model = this
        return binding.root
    }

    fun save() {
        val name = albumName.get().toString()
        if (name.isNotEmpty()) {
            // если апдейт альбома
            if (albumPosition !== null) {
                val item = model.albumData[albumPosition!!]
                item.name = name
                model.dataHelper.updateAlbum(item)
                model.albumDataObservable.onNext(object: Constants.AlbumAction {
                    override val action = "update"
                    override val data = item
                    override val position: Int = albumPosition!!
                })
            } else {
                val newPosition = model.albumData.size + 1
                // если добавление нового альбома
                val newAlbum = object: Constants.Album {
                    override val id: Int = newPosition
                    override var name = name
                    override var audioCount = 0
                }
                model.dataHelper.addAlbum(newAlbum)
                model.albumDataObservable.onNext(object: Constants.AlbumAction {
                    override val action = "add"
                    override val data = newAlbum
                    override val position: Int = newPosition
                }) }
            close()
        }
    }

    fun close() {
        ctx.parentFragmentManager.beginTransaction().remove(ctx).commit()
    }
}