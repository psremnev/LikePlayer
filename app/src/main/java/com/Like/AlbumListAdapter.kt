package com.Like

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView

class AlbumListAdapter(private val ctx: MainActivity): RecyclerView.Adapter<AlbumListAdapter.ViewHolder>()  {
    private var preHolder: ViewHolder? = null
    private val model = ViewModelProvider(ctx)[Model::class.java]
    private val dataHelper = DataHelper(ctx)
    private val holderList = HashMap<Int, ViewHolder>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.albumName)
        val count: TextView = itemView.findViewById(R.id.albumAudioCount)
        val albumInfo: LinearLayout = itemView.findViewById(R.id.albumInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.album_list_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemData = getItemData(position)

        // инициализируем текущий набор view
        if (!holderList.containsKey(position)) {
            holderList[position] = holder
        }

        // инициализируем данные шаблона
        holder.name.text = itemData.name
        holder.count.text = itemData.audioCount!!.toString()

        // инициализируем маркер
        if (itemData.id === Constants.AL_ALBUM_ID) {
            // изначально и при удалении сбрасываем маркер на альбом Все
            holder.albumInfo.setBackgroundColor(ContextCompat.getColor(ctx, R.color.black))
            preHolder = holder
        }

        // обработка лонг тап клика на альбом, открытие меню
        holder.itemView.setOnLongClickListener { v ->
            if (itemData.id !== Constants.AL_ALBUM_ID && itemData.id !== Constants.FAVORITE_ALBUM_ID) {
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
                            model.albumLiveData.value?.remove(itemData)
                            notifyItemRemoved(position)
                            // чтобы сменить маркер на предыдущий элемент и данные
                            val newPos = position - 1
                            // чтобы снять маркер если удаляем не переключаясь на запись
                            preHolder = holderList[newPos]
                            preHolder?.albumInfo?.setBackgroundColor(
                                ContextCompat.getColor(
                                    ctx,
                                    R.color.black
                                )
                            )
                            model.audioLiveData.value =
                                dataHelper.getAllAudioByAlbumId(getItemData(newPos).id!!)
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
            if (preHolder !== null) {
                preHolder!!.albumInfo.setBackgroundColor(ContextCompat.getColor(ctx, R.color.album_info))
            }
            model.audioLiveData.value = dataHelper.getAllAudioByAlbumId(itemData.id!!)
            holder.albumInfo.setBackgroundColor(ContextCompat.getColor(ctx, R.color.black))
            preHolder = holder
        }
    }

    override fun getItemCount(): Int {
        return model.albumLiveData.value!!.size
    }

    private fun getItemData(position: Int): Constants.Album {
        return model.albumLiveData.value!![position]
    }
}