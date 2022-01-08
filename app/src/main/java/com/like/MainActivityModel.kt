package com.like

import android.annotation.SuppressLint
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.like.addAlbumFragment.AddAlbumFragment
import com.like.adapters.AlbumListAdapter
import com.like.adapters.AudioListAdapter
import com.like.dataClass.Album
import com.like.dataClass.Audio
import rx.Observer
import rx.Subscription
import rx.schedulers.Schedulers
import rx.subjects.PublishSubject
import javax.inject.Inject

class MainActivityModel: ViewModel() {
    lateinit var ctx: MainActivity
    @Inject lateinit var dataModel: DataModel
    lateinit var albumLayoutManager: LinearLayoutManager
    lateinit var audioLayoutManager: LinearLayoutManager

    val playItemDataObservable: PublishSubject<Audio> = PublishSubject.create()
    val mediaPlayerStateChangedObservable: PublishSubject<Boolean> = PublishSubject.create()
    var playItemDataSubscription: Subscription? = null

    var audioData: ArrayList<Audio> = ArrayList()
    var albumData: ArrayList<Album> = ArrayList()
    var playItemData: Audio? = null

    lateinit var albumListAdapter: AlbumListAdapter
    lateinit var audioListAdapter: AudioListAdapter
    var playItemPosition: Int = 0
    var selectedAlbum: Int = 1
    var search: String = ""


    fun onCreate(ctx: MainActivity) {
        this.ctx = ctx

        val mainActivityComponent = (ctx.application as App).mainActivityComponent
        mainActivityComponent?.inject(this)

        ctx.binding.model = this
        initListAdapters()
        initSearch()
        if (ctx.savedInstanceState == null) {
            initNewMp3Songs()
            initModelData()
        } else {
            setElementsVisibility()
        }
        initLayoutManagers()
    }

    fun onResume() {
        subscribeOnDataChange()
    }

    fun onDestroy() {
        if (playItemDataSubscription != null) {
            playItemDataSubscription?.unsubscribe()
        }
    }

    fun initSearch() {
        if (search.isNotEmpty()) {
            ctx.binding.searchAudio.setQuery(search, false)
        }
    }

    fun initListAdapters() {
        albumListAdapter = AlbumListAdapter(ctx, albumData)
        audioListAdapter = AudioListAdapter(ctx, audioData)
    }

    private fun initLayoutManagers () {
        // нужно создаывать новые экземпляры иначе будет ошибка
        albumLayoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
        audioLayoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
    }

    @SuppressLint("Range")
    fun initNewMp3Songs() {
        val allSongsUri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val cursor: Cursor? =
            ctx.contentResolver.query(allSongsUri, null, null, null, selection)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val fileType = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE))
                    if (fileType == Constants.audioType) {
                        val id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                        val audioCursor = dataModel.getAudio(id)
                        if (audioCursor.count == 0) {
                            val baseName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                            dataModel.addAudio(Audio(
                                cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID)),
                                baseName.replace(Regex("""[.com.mp3]*"""), ""),
                                cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)),
                                cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
                                cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)),
                                cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)),
                                Constants.AL_ALBUM_ID
                            ))
                        }
                    }
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
    }

    fun updateNewAudio() {
        initNewMp3Songs()
        audioData.clear()
        audioData.addAll(dataModel.getAllAudioByAlbumId(Constants.AL_ALBUM_ID))
        setElementsVisibility()
        audioListAdapter.notifyDataSetChanged()
    }

    private fun subscribeOnDataChange() {
        playItemDataSubscription = playItemDataObservable
            .subscribeOn(Schedulers.newThread())
            .subscribe {
            playItemData = it
        }
    }

    private fun initModelData() {
        // инициализируем альбом по умолчанию
        dataModel.initDefaultAlbum(ctx)
        // инициализируем альбомы
        updateAlbumList()
        // инициализируем аудио лист
        updateAudioList()
    }

    fun addAlbum(data: Album, position: Int) {
        dataModel.addAlbum(data)
        albumData.add(data)
        albumListAdapter.notifyItemInserted(position)
    }

    fun updateAlbum(data: Album, position: Int) {
        dataModel.updateAlbum(data)
        albumData[position] = data
        albumListAdapter.notifyItemChanged(position)
    }

    fun deleteAlbum(data: Album, position: Int) {
        dataModel.deleteAlbum(data.id)
        albumData.remove(data)
        albumListAdapter.notifyItemRemoved(position)
        updateAlbumList()
    }

    fun updateAlbumList() {
        albumData.clear()
        dataModel.getAllAlbumObservable()
            .subscribe(object: Observer<Album> {
                override fun onCompleted() {
                    albumListAdapter.notifyDataSetChanged()
                }

                override fun onError(e: Throwable?) {
                    return
                }

                override fun onNext(t: Album?) {
                    albumData.add(t!!)
                }
            })
    }

    fun updateAudioList() {
        audioData.clear()
        val observer: Observer<Audio> = object: Observer<Audio> {
            override fun onCompleted() {

                if (audioData.size != 0 && playItemData == null) {
                    playItemData = audioData[playItemPosition]
                }
                setElementsVisibility()
                audioListAdapter.notifyDataSetChanged()
            }

            override fun onError(e: Throwable?) {
                Log.d("MainActivityModel", "UpdateAudioListError")
            }

            override fun onNext(t: Audio?) {
                audioData.add(t!!)
            }
        }
        if (search.isNotEmpty()) {
            dataModel.getAllAudioBySearchObservable(search)
                .subscribe(observer)
        } else {
            dataModel.getAllAudioByAlbumIdObservable(selectedAlbum)
                .subscribe(observer)
        }
    }

    fun addAlbum() {
        val addAlbumFrg: DialogFragment = AddAlbumFragment()
        addAlbumFrg.setStyle(DialogFragment.STYLE_NORMAL, R.style.ThemeOverlay_AppCompat_Dialog)
        addAlbumFrg.show(ctx.supportFragmentManager, "addAlbumDialog")
    }

    private fun setElementsVisibility() {
        val audioList = ctx.binding.audioList
        val audioPlay = ctx.binding.audioPlay
        val emptyView = ctx.binding.emptyAudioList
        val updateEmptyView = ctx.binding.emptyUpdateAudioList

        if (audioData.size != 0) {
            audioList.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
            updateEmptyView.visibility = View.GONE
        } else {
            audioList.visibility = View.GONE
            audioPlay.visibility = View.GONE
            if (selectedAlbum == Constants.AL_ALBUM_ID && search.isEmpty()) {
                updateEmptyView.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
            } else {
                updateEmptyView.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
            }
        }
        if (search.isEmpty()) {
            ctx.binding.albumListLayout.visibility = LinearLayout.VISIBLE
        } else {
            ctx.binding.albumListLayout.visibility = LinearLayout.GONE
        }
        if (playItemData == null) {
            audioPlay.visibility= View.GONE
        } else {
            audioPlay.visibility= View.VISIBLE
        }
    }

    fun onSearchTextChanged(newText: String?): Boolean {
        val search = ctx.binding.searchAudio
        val albumListLayout = ctx.binding.albumListLayout
        if (newText == "") {
            updateAudioList()
            albumListLayout.visibility = LinearLayout.VISIBLE
            search.isIconified = true
        }
        return false
    }


    fun onSearchTextSubmit(query: String?): Boolean {
        val albumListLayout = ctx.binding.albumListLayout
        query?.let {
            search = it
            updateAudioList()
        }
        albumListLayout.visibility = LinearLayout.GONE
        return true
    }
}