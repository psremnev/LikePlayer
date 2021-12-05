package com.Like

import android.annotation.SuppressLint
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private val dataHelper: DataHelper by lazy { DataHelper(this) }
    private val model: Model by lazy { ViewModelProvider(this).get() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState === null) {
            initNewMp3Songs()
            initModelData()
            initAddAlbumBtn()
            initAlbumList()
            initAudioList()
        } else {
            initAddAlbumBtn()
            initAlbumList()
            initAudioList()
        }
    }

    private fun initModelData() {
        model.audioLiveData.value = dataHelper?.getAllAudioByAlbumId(Constants.AL_ALBUM_ID)
        model.albumLiveData.value = dataHelper?.getAllAlbum()
        val audioData = model.audioLiveData.value
        if (audioData !== null && audioData?.size !== 0) {
            model.audioPlayItemLiveData.value = audioData[0]
        }
        model.dataHelper = dataHelper
    }

    @SuppressLint("Range")
    fun initNewMp3Songs() {
        val allSongsUri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val cursor: Cursor? =
            contentResolver.query(allSongsUri, null, null, null, selection)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val fileType = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE))
                    if (fileType == Constants.audioType) {
                        val id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                        val audioCursor = dataHelper.getAudio(id)
                        if (audioCursor.count === 0) {
                            dataHelper.addAudio(object : Constants.Audio {
                                override val id =
                                    cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                                override val name =
                                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                                override val duration =
                                    cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                                override val artist =
                                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                                override val url =
                                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                                override val albumId: Long = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                                override val album = Constants.AL_ALBUM_ID
                            })
                        }
                    }
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
    }

    private fun initAddAlbumBtn() {
        val addAlbumBtn: Button = findViewById(R.id.addAlbumBtn)
        addAlbumBtn.setOnClickListener {
            val addAlbumFrg: DialogFragment = AddAlbumDialog()
            addAlbumFrg.show(supportFragmentManager, "addAlbum")
        }
    }

    private fun initAlbumList() {
        dataHelper?.initDefaultAlbum(this)
        val albumList: RecyclerView = findViewById(R.id.albumList)
        albumList.layoutManager =
            LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
        val albumLiveData: MutableLiveData<ArrayList<Constants.Album>>? = model?.getAlbumData()
        albumList.adapter = AlbumListAdapter(this, supportFragmentManager)
        albumLiveData?.observe(this, {
            albumList.adapter?.notifyDataSetChanged()
        })
    }

    private fun initAudioList() {
        val audioList: ListView = findViewById(R.id.audioList)
        val audioPlay: View? = findViewById(R.id.audioPlay)
        val emptyView: TextView? = findViewById(R.id.emptyAudioList)
        var isVisible: Boolean
        var audioData: MutableLiveData<ArrayList<Constants.Audio>>? = model?.getAudioData()

        // установка видимости компонентов исходя из данных
        val setVisibility: (audioData: ArrayList<Constants.Audio>?) -> Boolean =  {
            if (audioData !== null && audioData?.value?.size !== 0) {
                audioPlay?.visibility = View.VISIBLE
                audioList.visibility = View.VISIBLE
                emptyView?.visibility = View.GONE
                true
            } else {
                audioPlay?.visibility = View.GONE
                audioList.visibility = View.GONE
                emptyView?.visibility = View.VISIBLE
                false
            }
        }

        // инициализация
        isVisible = setVisibility(audioData?.value)
        if (isVisible) {
            val adapter = AudioListAdapter(this)
            audioList.adapter = adapter
        }

        // обновление данных по альбому
        audioData?.observe(this, {
            val newAudioData = model.audioLiveData
            isVisible = setVisibility(newAudioData?.value)
            if (audioData !== newAudioData?.value) {
                audioData = newAudioData
            }
        })
    }
}