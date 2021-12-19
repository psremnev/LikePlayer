package com.Like

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get

class AddAlbumDialog(): DialogFragment() {
    var albumPosition: Int? = null
    var albumName: String? = null
    var dataHelper: DataHelper? = null
    var model: Model? = null
    var albumLiveData: MutableLiveData<ArrayList<Constants.Album>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        model = ViewModelProvider(activity as MainActivity).get()
        dataHelper = DataHelper(requireContext())
        albumLiveData = model?.albumLiveData
        albumPosition = arguments?.getInt("position")
        albumName = arguments?.getString("name")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_album_dialog, container, false)
    }

    override fun onStart() {
        super.onStart()
        val editTextName: EditText? = view?.findViewById(R.id.addAlbumDialogName)
        editTextName?.setText(albumName)
        editTextName?.addTextChangedListener( object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                return
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                return
            }

            override fun afterTextChanged(s: Editable?) {
                albumName = s.toString()
            }
        })

        val saveBtn: Button? = view?.findViewById(R.id.addAlbumDialogSave)
        saveBtn?.setOnClickListener {
            if (albumName?.length!! > 0) {
                // если апдейт альбома
                if (albumPosition !== null) {
                    val item = albumLiveData?.value?.elementAt(albumPosition!!)
                    item?.name = albumName.toString()
                    dataHelper?.updateAlbum(item!!)
                    albumLiveData?.value = dataHelper?.getAllAlbum()
                } else {
                    // если добавление нового альбома
                    val newAlbum = object: Constants.Album {
                        override val id: Int? = albumLiveData?.value?.size!! + 1
                        override var name = albumName.toString()
                        override var audioCount = 0
                    }
                    dataHelper?.addAlbum(newAlbum)
                    albumLiveData?.value = dataHelper?.getAllAlbum()
                }
                close()
            }
        }
        val cancelBtn: Button? = view?.findViewById(R.id.addAlbumDialogCancel)
        cancelBtn?.setOnClickListener {
            close()
        }
    }

    private fun close() {
        parentFragmentManager.beginTransaction().remove(this).commit()
    }
}