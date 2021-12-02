package com.Like

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView

class AlbumListAdapter(private val ctx: MainActivity, val frgManager: FragmentManager): RecyclerView.Adapter<AlbumListAdapter.ViewHolder>()  {
    var preHolder: ViewHolder? = null
    private val model = ctx.getModel()
    val dataHelper = DataHelper(ctx)
    val albumLiveData = model?.getAlbumData()
    val audioLiveData = model?.getAudioData()

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
        val position = holder.adapterPosition
        val itemData = getItemData(position)

        holder.name.text = itemData.name
        holder.count.text = itemData.audioCount!!.toString()

        if (itemData.id === Constants.AL_ALBUM_ID) {
            holder.albumInfo.setBackgroundColor(ContextCompat.getColor(ctx, R.color.black))
            preHolder = holder
        }

        holder.itemView.setOnLongClickListener(object: View.OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                if (itemData.id !== Constants.AL_ALBUM_ID && itemData.id !== Constants.FAVORITE_ALBUM_ID) {
                    val popup= PopupMenu(v?.context, v);
                    popup.inflate(R.menu.album);
                    popup.setOnMenuItemClickListener { item ->
                        when (item?.itemId) {
                            R.id.edit -> {
                                val addAlbumFrg: DialogFragment =
                                    AddAlbumDialog(this@AlbumListAdapter)
                                val args = Bundle()
                                args.putInt("position", position)
                                args.putString("name", itemData.name)
                                addAlbumFrg.arguments = args
                                addAlbumFrg.show(frgManager!!, "addAlbum")
                            }
                            R.id.delete -> {
                                dataHelper.deleteAlbum(itemData.id)
                                albumLiveData?.value?.remove(itemData)
                                notifyItemRemoved(position)
                            }
                        }
                        if (item?.itemId === R.id.edit) {

                        }
                        true
                    };
                    popup.show();
                }
                return true
            }
        })
        holder.itemView.setOnClickListener {
            if (preHolder !== null) {
                preHolder!!.albumInfo.setBackgroundColor(ContextCompat.getColor(ctx, R.color.album_info))
            }
            audioLiveData?.value = dataHelper?.getAllAudioByAlbumId(itemData.id!!)
            holder.albumInfo.setBackgroundColor(ContextCompat.getColor(ctx, R.color.black))
            preHolder = holder
        }
    }

    override fun getItemCount(): Int {
        return albumLiveData?.value!!.size
    }

    private fun getItemData(position: Int): Constants.Album {
        return albumLiveData?.value!![position]
    }
}