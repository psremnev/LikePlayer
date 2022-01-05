package com.like.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.like.*
import com.like.addAlbumDialog.AddAlbumDialog
import com.like.dataClass.Album
import com.like.databinding.AlbumListItemBinding
import javax.inject.Inject

class AlbumListAdapter(val ctx: MainActivity, val data: ArrayList<Album>): RecyclerView.Adapter<AlbumListAdapter.ViewHolder>()  {
    @Inject lateinit var model: MainActivityModel
    private var albumPreHolder: ViewHolder? = null
    private var albumHolderList: HashMap<Int, ViewHolder> = HashMap()

    init {
        val mainActivityComponent = (ctx.application as App).mainActivityComponent
        mainActivityComponent?.inject(this)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: AlbumListItemBinding? = DataBindingUtil.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AlbumListItemBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.album_list_item,
            parent,
            false
        )
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val position: Int = holder.adapterPosition
        val itemData = getItemData(position)
        val listeners = object: AlbumListeners {
            override fun onClick(view: View) {
                model.selectedAlbum = itemData.id!!
                if (albumPreHolder !== null) {
                    albumPreHolder?.binding?.marked = false
                }
                albumPreHolder = holder
                holder.binding?.marked = true
                model.audioDataObservable.onNext(model.dataModel.getAllAudioByAlbumId(model.selectedAlbum))
            }

            override fun onLongTapClick(view: View): Boolean {
                val popup = PopupMenu(view.context, view)
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
                            model.dataModel.deleteAlbum(itemData.id)
                            model.albumDataObservable.onNext(object: AlbumAction {
                                override val action = "delete"
                                override val data = itemData
                                override val position = position
                            })
                            notifyItemRemoved(position)
                            // чтобы сменить маркер на предыдущий элемент и данные
                            val newPos = position - 1

                            // если маркер на альбоме который удалем то надо сменить его
                            if (albumHolderList[position]?.binding?.marked == true && newPos >= 0) {
                                albumPreHolder?.binding?.marked = false // снимаем текущий маркер
                                // ставим маркер на новый элемент
                                albumPreHolder = albumHolderList[newPos]
                                albumPreHolder?.binding?.marked = true
                                // получаем данные по новому альбому
                                model.audioDataObservable.onNext(model.dataModel.getAllAudioByAlbumId(getItemData(newPos).id!!))
                            }
                        }
                        Constants.updateItemId -> {
                            model.updateAllAudioList()
                        }
                    }
                    true
                };
                popup.show();
                return true
            }
        }

        holder.binding?.itemData = itemData
        holder.binding?.marked = false
        holder.binding?.listeners = listeners
        // инициализируем текущий набор view
        if (!albumHolderList.containsKey(position)) {
            albumHolderList[position] = holder
        }

        // инициализируем маркер
        if (itemData.id == model.selectedAlbum) {
            // изначально и при удалении сбрасываем маркер на альбом Все
            holder.binding?.marked = true
            albumPreHolder = holder
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun getItemData(position: Int): Album {
        return data[position]
    }
}