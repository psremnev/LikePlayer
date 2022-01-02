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
import com.like.selectAlbumDialog.SelectAlbumDialog
import com.like.utils.DataHelper
import java.io.FileNotFoundException
import rx.subjects.PublishSubject

class MainActivityModel: ViewModel() {
    lateinit var ctx: MainActivity
    val dataHelper by lazy { DataHelper(ctx) }
    private var albumHolderList: HashMap<Int, AlbumListAdapter.ViewHolder> = HashMap<Int, AlbumListAdapter.ViewHolder>()
    private var albumPreHolder: AlbumListAdapter.ViewHolder? = null
    lateinit var albumLayoutManager: LinearLayoutManager
    lateinit var audioLayoutManager: LinearLayoutManager

    val albumDataObservable: PublishSubject<Constants.AlbumAction> = PublishSubject.create()
    val audioDataObservable: PublishSubject<ArrayList<Constants.Audio>> = PublishSubject.create()
    val playItemDataObservable: PublishSubject<Constants.Audio> = PublishSubject.create()
    var audioData: ArrayList<Constants.Audio> = ArrayList()
    var albumData: ArrayList<Constants.Album> = ArrayList()
    lateinit var playItemData: Constants.Audio

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

    private fun subscribeOnObservable() {
        albumDataObservable.subscribe {
            when (it.action) {
                "updateAll" -> {
                    albumData.clear()
                    albumData.addAll(it.data as ArrayList<Constants.Album>)
                    albumListAdapter.notifyDataSetChanged()
                }
                "add" -> {
                    albumData.add(it.data as Constants.Album)
                    albumListAdapter.notifyItemInserted(it.position!!)
                }
                "delete" -> {
                    albumData.remove(it.data as Constants.Album)
                    albumListAdapter.notifyItemRemoved(it.position!!)
                }
                "update" -> {
                    albumData[it.position!!] = it.data as Constants.Album
                    albumListAdapter.notifyItemChanged(it.position!!)
                }
            }
        }
        audioDataObservable.subscribe {
            audioData.clear()
            audioData.addAll(it)
            audioListAdapter.notifyDataSetChanged()
        }

        playItemDataObservable.subscribe {
            playItemData = it
        }
    }

    private fun initModelData() {
        dataHelper.initDefaultAlbum(ctx)
        albumData = dataHelper.getAllAlbum()
        audioData = dataHelper.getAllAudioByAlbumId(Constants.AL_ALBUM_ID)
        if (audioData.size != 0) {
            playItemData = audioData[playItemPosition]
        }
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

        if (audioData.size != 0) {
            audioList.visibility = View.VISIBLE
            audioPlay.visibility= View.VISIBLE
            emptyView.visibility = View.GONE
        } else {
            audioList.visibility = View.GONE
            audioPlay.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        }
    }

    fun onSearchTextChanged(newText: String?): Boolean {
        val search = ctx.binding.searchAudio
        val albumListLayout = ctx.binding.albumListLayout
        if (newText == "") {
            audioDataObservable.onNext(dataHelper.getAllAudioByAlbumId(selectedAlbum))
            albumListLayout.visibility = LinearLayout.VISIBLE
            search.isIconified = true
        }
        return false
    }

    fun onSearchTextSubmit(query: String?): Boolean {
        val albumListLayout = ctx.binding.albumListLayout
        query?.let {
            audioDataObservable.onNext(dataHelper.getAllAudioBySearch(it))
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
        holder.count.text = itemData.audioCount!!.toString()

        // инициализируем маркер
        if (itemData.id == selectedAlbum) {
            // изначально и при удалении сбрасываем маркер на альбом Все
            holder.albumInfo.background = ctx.getDrawable(R.drawable.album_background_selected)
            albumPreHolder = holder
        }

        // обработка лонг тап клика на альбом, открытие меню
        holder.itemView.setOnLongClickListener { v ->
            if (itemData.id != Constants.AL_ALBUM_ID) {
                val popup = PopupMenu(v?.context, v);
                popup.inflate(R.menu.album);
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
                            dataHelper.deleteAlbum(itemData.id)
                            albumDataObservable.onNext(object: Constants.AlbumAction {
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
                            audioDataObservable.onNext(dataHelper.getAllAudioByAlbumId(adapter.getItemData(newPos).id!!))
                        }
                    }
                    true
                };
                popup.show();
            }
            true
        }

        // обработка клика на альбом
        holder.itemView.setOnClickListener {
            selectedAlbum = itemData.id!!
            if (albumPreHolder !== null) {
                albumPreHolder!!.albumInfo.background = ctx.getDrawable(R.drawable.album_background)
            }
            audioDataObservable.onNext(dataHelper.getAllAudioByAlbumId(selectedAlbum))
            holder.albumInfo.background = ctx.getDrawable(R.drawable.album_background_selected)
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
                playItemDataObservable.onNext(itemData)
                playItemPosition = position
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