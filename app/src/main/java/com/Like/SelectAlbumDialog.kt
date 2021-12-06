package com.Like

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get

class SelectAlbumDialog : DialogFragment() {

    private val model: Model by lazy { ViewModelProvider(activity as MainActivity).get() }
    private var audioPos: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        audioPos = arguments?.getInt("audioPos")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.select_album_dialog, container, false)
    }

    override fun onStart() {
        super.onStart()

        val list: ListView? = view?.findViewById(R.id.selectAlbumList)
        list?.choiceMode = ListView.CHOICE_MODE_SINGLE;
        val albumNames = ArrayList<Any>()
        for (album in model.albumLiveData.value!!) {
            if (album.id !== Constants.AL_ALBUM_ID && album.id !== Constants.FAVORITE_ALBUM_ID) {
                albumNames.add(album.name)
            }
        }
        val adapter: ArrayAdapter<Any> = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_single_choice,
            albumNames
        )
        list?.adapter = adapter
        list?.setOnItemClickListener { parent, view, position, id ->
            val defAlbumCount = 2
            val albumPos = position + defAlbumCount
            val albumItem = model.albumLiveData.value!![albumPos]
            albumItem.audioCount = albumItem.audioCount?.plus(1)
            val audioItem: Constants.Audio = model.audioLiveData.value!![audioPos!!]
            audioItem.album = albumItem.id!!
            model.dataHelper?.updateAudio(audioItem)
            (activity as MainActivity).updateAlbum(albumItem, albumPos)
        }
    }
}