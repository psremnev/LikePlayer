package com.like.addAlbumFragment

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.like.App
import com.like.MainActivityModel
import com.like.databinding.AddAlbumFragmentBinding
import com.like.dataClass.Album
import javax.inject.Inject

class AddAlbumFragmentModel: ViewModel() {

    lateinit var binding: AddAlbumFragmentBinding
    private lateinit var ctx: AddAlbumFragment
    var albumPosition: Int? = null
    var albumName: ObservableField<String> = ObservableField<String>("")
    @Inject lateinit var model: MainActivityModel
    var isAdd: Boolean = false

    fun onCreateView(ctx: AddAlbumFragment) {
        this.ctx = ctx
        val mainActivityComponent = (ctx.activity?.application as App).mainActivityComponent
        mainActivityComponent?.inject(this)

        albumPosition = ctx.arguments?.getInt("position")
        albumName.set(ctx.arguments?.getString("name"))
        isAdd = albumName.get() == null
        ctx.binding.model = this
    }

    fun save() {
        val name = albumName.get().toString()
        if (name.isNotEmpty()) {
            // если апдейт альбома
            if (isAdd) {
                val newPosition = model.albumData.size + 1
                // если добавление нового альбома
                val newAlbum = Album(newPosition, name, 0)
                model.addAlbum(newAlbum, newPosition)
            } else {
                val item = model.albumData[albumPosition!!]
                item.name = name
                model.updateAlbum(item, albumPosition!!)
            }
            close()
        }
    }

    fun close() {
        ctx.parentFragmentManager.beginTransaction().remove(ctx).commit()
    }
}