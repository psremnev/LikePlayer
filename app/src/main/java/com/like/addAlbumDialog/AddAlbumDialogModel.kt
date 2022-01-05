package com.like.addAlbumDialog

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.like.App
import com.like.Interfaces
import com.like.MainActivityModel
import com.like.databinding.AddAlbumFragmentBinding
import com.like.dataClass.Album
import javax.inject.Inject

class AddAlbumDialogModel: ViewModel() {

    lateinit var binding: AddAlbumFragmentBinding
    private lateinit var ctx: AddAlbumDialog
    var albumPosition: Int? = null
    var albumName: ObservableField<String> = ObservableField<String>("")
    @Inject lateinit var model: MainActivityModel

    fun onCreateView(ctx: AddAlbumDialog) {
        this.ctx = ctx
        val mainActivityComponent = (ctx.activity?.application as App).mainActivityComponent
        mainActivityComponent?.inject(this)

        albumPosition = ctx.arguments?.getInt("position")
        albumName.set(ctx.arguments?.getString("name"))
        ctx.binding.model = this
    }

    fun save() {
        val name = albumName.get().toString()
        if (name.isNotEmpty()) {
            // если апдейт альбома
            if (albumPosition !== null) {
                val item = model.albumData[albumPosition!!]
                item.name = name
                model.dataModel.updateAlbum(item)
                model.albumDataObservable.onNext(object: Interfaces.AlbumAction {
                    override val action = "update"
                    override val data = item
                    override val position: Int = albumPosition!!
                })
            } else {
                val newPosition = model.albumData.size + 1
                // если добавление нового альбома
                val newAlbum = Album(newPosition, name, 0)
                model.dataModel.addAlbum(newAlbum)
                model.albumDataObservable.onNext(object: Interfaces.AlbumAction {
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