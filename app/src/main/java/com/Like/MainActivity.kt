package com.Like

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
        dataHelper.initDefaultAlbum(this)
        albumList.layoutManager =
            LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
        val albumLiveData: MutableLiveData<ArrayList<Constants.Album>> = model.albumLiveData
        albumList.adapter = AlbumListAdapter(this)
        albumLiveData.observe(this, {
            albumList.adapter?.notifyDataSetChanged()
        })
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