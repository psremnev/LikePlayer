package com.like.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.like.Constants
import com.like.MainActivity
import com.like.MainActivityModel
import com.like.R

class AlbumListAdapter(ctx: MainActivity, val data: ArrayList<Constants.Album>): RecyclerView.Adapter<AlbumListAdapter.ViewHolder>()  {
    private val model = ViewModelProvider(ctx)[MainActivityModel::class.java]

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
        model.onBindAlbumListViewHolder(this, holder, position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun getItemData(position: Int): Constants.Album {
        return data[position]
    }
}