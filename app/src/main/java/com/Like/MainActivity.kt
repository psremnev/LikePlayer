package com.Like

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.SearchView
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
    private val albumList: RecyclerView by lazy { findViewById(R.id.albumList) }
    private val audioList: RecyclerView by lazy { findViewById(R.id.audioList) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState === null) {
            initNewMp3Songs()
            initModelData()
            initAddAlbumBtn()
            initAlbumList()
            initAudioList()
            initSearch()
        } else {
            initAddAlbumBtn()
            initAlbumList()
            initAudioList()
            initSearch()
        }
    }

    override fun onResume() {
        super.onResume()
        // ставим обычную ориентацию и тему после splash экрана
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        setTheme(R.style.Theme_Like)
    }

    private fun initSearch() {
        //TODO: Пока не работает, неправильный запрос
        val search: SearchView = findViewById(R.id.searchAudio)
        search.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                model.audioLiveData.value = query?.let { model.albumLiveData.value!![model.selectedAlbum].id?.let { it1 ->
                    dataHelper.getAllAudioBySearch(
                        it1, it)
                } }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
    }

    private fun initModelData() {
        dataHelper.initDefaultAlbum(this)
        model.audioLiveData.value = dataHelper.getAllAudioByAlbumId(Constants.AL_ALBUM_ID)
        model.albumLiveData.value = dataHelper.getAllAlbum()
        val audioData = model.audioLiveData.value
        if (audioData !== null && audioData.size !== 0) {
            model.audioPlayItemLiveData.value = audioData[0]
        }
        model.dataHelper = dataHelper
    }

    private fun initAddAlbumBtn() {
        val addAlbumBtn: Button = findViewById(R.id.addAlbumBtn)
        addAlbumBtn.setOnClickListener {
            val addAlbumFrg: DialogFragment = AddAlbumDialog()
            addAlbumFrg.setStyle(DialogFragment.STYLE_NORMAL, R.style.ThemeOverlay_AppCompat_Dialog)
            addAlbumFrg.show(supportFragmentManager, "addAlbum")
        }
    }

    private fun initAlbumList() {
        albumList.layoutManager =
            LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
        val albumLiveData: MutableLiveData<ArrayList<Constants.Album>> = model.albumLiveData
        albumList.adapter = AlbumListAdapter(this)
        albumLiveData.observe(this, {
            albumList.adapter?.notifyDataSetChanged()
        })
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
                            val baseName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                            dataHelper.addAudio(object : Constants.Audio {
                                override val id =
                                    cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                                override val name = baseName.replace(Regex("""[.com.mp3]*"""), "")
                                override val duration =
                                    cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                                override val artist =
                                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                                override val url =
                                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                                override val albumId: Long = cursor.getLong(cursor.getColumnIndex(
                                    MediaStore.Audio.Media.ALBUM_ID))
                                override var album = Constants.AL_ALBUM_ID
                            })
                        }
                    }
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
    }

    private fun initAudioList() {
        audioList.layoutManager =
            LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        val audioPlay: View? = findViewById(R.id.audioPlay)
        val emptyView: TextView? = findViewById(R.id.emptyAudioList)
        var isVisible: Boolean
        var audioData: MutableLiveData<ArrayList<Constants.Audio>>? = model.audioLiveData

        // установка видимости компонентов исходя из данных
        val setVisibility: (audioData: ArrayList<Constants.Audio>?) -> Boolean =  {
            if (audioData !== null && audioData?.value?.size !== 0) {
                audioList.visibility = View.VISIBLE
                audioPlay?.visibility = View.VISIBLE
                emptyView?.visibility = View.GONE
                true
            } else {
                audioList.visibility = View.GONE
                audioPlay?.visibility = View.GONE
                emptyView?.visibility = View.VISIBLE
                false
            }
        }

        // инициализация
        isVisible = setVisibility(audioData?.value)
        if (isVisible) {
            audioList.adapter = AudioListAdapter(this)
        }

        // обновление данных по альбому
        audioData?.observe(this, {
            val newAudioData = model.audioLiveData
            isVisible = setVisibility(newAudioData.value)
            if (audioData !== newAudioData?.value) {
                audioData = newAudioData
            }
            audioList.scrollToPosition(model.playItemPos)
        })
    }
}