package com.like.selectAlbumDialog

import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.lifecycle.ViewModel
import com.like.App
import com.like.Interfaces
import com.like.MainActivityModel
import com.like.dataClass.Audio
import javax.inject.Inject

class SelectAlbumDialogModel: ViewModel() {
    lateinit var ctx: SelectAlbumDialog
    @Inject lateinit var model: MainActivityModel
    var audioPosition: Int? = null
    lateinit var adapter: ArrayAdapter<Any>
    var choiceMode: Int =  ListView.CHOICE_MODE_SINGLE;

    fun onCreateView(ctx:SelectAlbumDialog) {
        this.ctx = ctx
        val mainActivityComponent = (ctx.activity?.application as App).mainActivityComponent
        mainActivityComponent?.inject(this)
        ctx.binding.model = this
    }

    fun onCreate(ctx: SelectAlbumDialog) {
        audioPosition = ctx.arguments?.getInt("audioPosition")
    }

    fun onStart() {
        initAdapter()
        ctx.binding.selectAlbumList.setSelection(model.selectedAlbum - 1)
        ctx.binding.selectAlbumList.setOnItemClickListener {
                _, _, position, _ -> onItemClick(position)
        }
    }

    private fun initAdapter() {
        val albumNames = ArrayList<Any>()
        for (album in model.albumData) {
            albumNames.add(album.name)
        }
        adapter = ArrayAdapter(ctx.requireContext(), android.R.layout.simple_list_item_single_choice,
            albumNames
        )
    }

    private fun onItemClick(position: Int) {
        val albumItem = model.albumData[position]
        albumItem.audioCount = albumItem.audioCount.plus(1)
        val audioItem: Audio = model.playItemData!!
        audioItem.album = albumItem.id!!
        model.dataModel.updateAudio(audioItem)

        model.albumDataObservable.onNext(object: Interfaces.AlbumAction {
            override val action = "updateAll"
            override val data = model.dataModel.getAllAlbum()
        })

        model.audioDataObservable.onNext(model.dataModel.getAllAudioByAlbumId(model.selectedAlbum))
    }
}