package com.like

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.pm.ActivityInfo
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.like.addAlbumDialog.AddAlbumDialog
import com.like.adapters.AlbumListAdapter
import com.like.adapters.AudioListAdapter
import com.like.dataClass.Album
import com.like.dataClass.Audio
import com.like.selectAlbumDialog.SelectAlbumDialog
import rx.Observer
import rx.schedulers.Schedulers
import java.io.FileNotFoundException
import rx.subjects.PublishSubject

class MainActivityModel: ViewModel() {
    lateinit var ctx: MainActivity
    val dataModel by lazy { DataModel(ctx) }
    private var albumHolderList: HashMap<Int, AlbumListAdapter.ViewHolder> = HashMap<Int, AlbumListAdapter.ViewHolder>()
    private var albumPreHolder: AlbumListAdapter.ViewHolder? = null
    lateinit var albumLayoutManager: LinearLayoutManager
    lateinit var audioLayoutManager: LinearLayoutManager

    val albumDataObservable: PublishSubject<Interfaces.AlbumAction> = PublishSubject.create()
    val audioDataObservable: PublishSubject<ArrayList<Audio>> = PublishSubject.create()
    val playItemDataObservable: PublishSubject<Audio> = PublishSubject.create()
    val mediaPlayerStateChangedObservable: PublishSubject<Boolean> = PublishSubject.create()

    var audioData: ArrayList<Audio> = ArrayList()
    var albumData: ArrayList<Album> = ArrayList()
    var playItemData: Audio? = null

    val albumListAdapter: AlbumListAdapter by lazy { AlbumListAdapter(ctx, albumData) }
    val audioListAdapter: AudioListAdapter by lazy { AudioListAdapter(ctx, audioData) }
    var playItemPosition: Int = 0
    var selectedAlbum: Int = 1


    fun onCreate(ctx: MainActivity) {
        this.ctx = ctx
        ctx.binding.model = this
        if (ctx.savedInstanceState == null) {
            initNewMp3Songs()
            initModelData()
        }
        initLayoutManagers()
        setElementsVisibility()
    }

    fun onResume(ctx: MainActivity) {
        // ставим обычную ориентацию и тему после splash экрана
        ctx.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        ctx.setTheme(R.style.Theme_Like)
        subscribeOnObservable()
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

    fun updateAllAudioList() {
        initNewMp3Songs()
        audioData.clear()
        audioData.addAll(dataModel.getAllAudioByAlbumId(Constants.AL_ALBUM_ID))
        setElementsVisibility()
        audioListAdapter.notifyDataSetChanged()
    }

    private fun subscribeOnObservable() {
        albumDataObservable
            .subscribeOn(Schedulers.newThread())
            .subscribe {
            when (it.action) {
                "updateAll" -> {
                    albumData.clear()
                    albumData.addAll(it.data as ArrayList<Album>)
                    albumListAdapter.notifyDataSetChanged()
                }
                "add" -> {
                    albumData.add(it.data as Album)
                    albumListAdapter.notifyItemInserted(it.position!!)
                }
                "delete" -> {
                    albumData.remove(it.data as Album)
                    albumListAdapter.notifyItemRemoved(it.position!!)
                }
                "update" -> {
                    albumData[it.position!!] = it.data as Album
                    albumListAdapter.notifyItemChanged(it.position!!)
                }
            }
        }
        audioDataObservable
            .subscribeOn(Schedulers.newThread())
            .subscribe {
            audioData.clear()
            audioData.addAll(it)
            setElementsVisibility()
            audioListAdapter.notifyDataSetChanged()
        }

        playItemDataObservable
            .subscribeOn(Schedulers.newThread())
            .subscribe {
            playItemData = it
        }
    }

    private fun initModelData() {
        // инициализируем альбом по умолчанию
        dataModel.initDefaultAlbum(ctx)
        // инициализируем альбомы
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
        // инициализируем аудио лист
        dataModel.getAllAudioByAlbumIdObservable(Constants.AL_ALBUM_ID)
            .subscribe(object: Observer<Audio> {
                override fun onCompleted() {
                    if (audioData.size != 0) {
                        playItemData = audioData[playItemPosition]
                    }
                    audioListAdapter.notifyDataSetChanged()
                }

                override fun onError(e: Throwable?) {
                    return
                }

                override fun onNext(t: Audio?) {
                    audioData.add(t!!)
                }
        })
    }

    fun addAlbum() {
        val addAlbumFrg: DialogFragment = AddAlbumDialog()
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
            if (selectedAlbum == Constants.AL_ALBUM_ID) {
                updateEmptyView.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
            } else {
                updateEmptyView.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
            }
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
            audioDataObservable.onNext(dataModel.getAllAudioByAlbumId(selectedAlbum))
            albumListLayout.visibility = LinearLayout.VISIBLE
            search.isIconified = true
        }
        return false
    }

    fun onSearchTextSubmit(query: String?): Boolean {
        val albumListLayout = ctx.binding.albumListLayout
        query?.let {
            audioDataObservable.onNext(dataModel.getAllAudioBySearch(it))
        }
        albumListLayout.visibility = LinearLayout.GONE
        return true
    }

    fun onBindAlbumListViewHolder(adapter: AlbumListAdapter, holder: AlbumListAdapter.ViewHolder, position: Int) {
        val itemData = adapter.getItemData(position)

        // инициализируем текущий набор view
        if (!albumHolderList.containsKey(position)) {
            albumHolderList[position] = holder
        }

        // инициализируем данные шаблона
        holder.name.text = itemData.name
        holder.count.text = itemData.audioCount.toString()

        // инициализируем маркер
        if (itemData.id == selectedAlbum) {
            // изначально и при удалении сбрасываем маркер на альбом Все
            //holder.albumInfo.background = ctx.getDrawable(R.drawable.album_background_selected)
            albumPreHolder = holder
        }

        // обработка лонг тап клика на альбом, открытие меню
        holder.itemView.setOnLongClickListener { v ->
                val popup = PopupMenu(v?.context, v)
                if (itemData.id == Constants.AL_ALBUM_ID) {
                    popup.menu.add(1, Constants.updateItemId, 1, R.string.emptyUpdateAudioListBtn)
                } else {
                    popup.inflate(R.menu.album)
                }
                popup.setOnMenuItemClickListener { item ->
                    when (item?.itemId) {
                        R.id.edit -> {
                            val addAlbumFrg: DialogFragment = AddAlbumDialog()
                            addAlbumFrg.setStyle(
                                DialogFragment.STYLE_NORMAL,
                                R.style.ThemeOverlay_AppCompat_Dialog
                            )
                            val args = Bundle()
                            args.putInt("position", position)
                            args.putString("name", itemData.name)
                            addAlbumFrg.arguments = args
                            addAlbumFrg.show(ctx.supportFragmentManager, "addAlbum")
                        }
                        R.id.delete -> {
                            dataModel.deleteAlbum(itemData.id)
                            albumDataObservable.onNext(object: Interfaces.AlbumAction {
                                override val action = "delete"
                                override val data = itemData
                                override val position = position
                            })
                            adapter.notifyItemRemoved(position)
                            // чтобы сменить маркер на предыдущий элемент и данные
                            val newPos = position - 1
                            // чтобы снять маркер если удаляем не переключаясь на запись
                            albumPreHolder = albumHolderList[newPos]
                            albumPreHolder?.albumInfo?.background = ctx.getDrawable(R.drawable.album_background_selected)
                            audioDataObservable.onNext(dataModel.getAllAudioByAlbumId(adapter.getItemData(newPos).id!!))
                        }
                        Constants.updateItemId -> {
                            updateAllAudioList()
                        }
                    }
                    true
                };
                popup.show();
            true
        }

        // обработка клика на альбом
        holder.itemView.setOnClickListener {
            selectedAlbum = itemData.id!!
            /*if (albumPreHolder !== null) {
                albumPreHolder!!.albumInfo.background = ctx.getDrawable(R.drawable.album_background)
            }*/
            audioDataObservable.onNext(dataModel.getAllAudioByAlbumId(selectedAlbum))
            //holder.albumInfo.background = ctx.getDrawable(R.drawable.album_background_selected)
            albumPreHolder = holder
        }
    }

    fun onBindAudioListViewHolder(adapter: AudioListAdapter, holder: AudioListAdapter.ViewHolder, position: Int) {
        val itemData = adapter.getItemData(position)
        val setArtist = {
            if (itemData.artist != Constants.unknownArtist) {
                holder.artist?.text = itemData.artist
            } else {
                holder.artist?.text = ""
            }
        }
        val setPhoto = {
            val sArtworkUri = Uri.parse(Constants.ALBUM_ART_URI)
            val uri: Uri = ContentUris.withAppendedId(sArtworkUri, itemData.albumId)
            try {
                val inputStr = ctx.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStr)
                holder.emptyAlbumPhoto.visibility = View.GONE
                holder.image?.setImageBitmap(bitmap)
            } catch (e: FileNotFoundException) {
                holder.emptyAlbumPhoto.visibility = View.VISIBLE
            }
        }

        val setListeners = {
            holder.audioPlayContent.setOnClickListener {
                playItemPosition = position
                playItemDataObservable.onNext(itemData).run {
                    mediaPlayerStateChangedObservable.onNext(true)
                }
            }
            holder.menu?.setOnClickListener{
                val args = Bundle()
                args.putInt("audioPosition", position)
                val frg = SelectAlbumDialog()
                frg.setStyle(DialogFragment.STYLE_NORMAL, R.style.ThemeOverlay_AppCompat_Dialog)
                frg.arguments = args
                frg.show(ctx.supportFragmentManager, "selectAlbum")
            }
        }

        holder.name?.text = itemData.name
        setArtist()
        setPhoto()
        setListeners()
        if (holder.image?.drawable === null) {
            holder.emptyAlbumPhoto.visibility = View.VISIBLE
        }
    }
}