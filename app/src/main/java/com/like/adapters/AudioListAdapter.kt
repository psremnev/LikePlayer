package com.like.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.like.Constants
import com.like.MainActivity
import com.like.MainActivityModel
import com.like.R

class AudioListAdapter(ctx: MainActivity, val data: ArrayList<Constants.Audio>):
    RecyclerView.Adapter<AudioListAdapter.ViewHolder>() {
    private val model = ViewModelProvider(ctx)[MainActivityModel::class.java]

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val audioPlayContent: LinearLayout = itemView.findViewById(R.id.audioPlayContent)
        val image: ImageView? = itemView.findViewById(R.id.audioImage)
        val name: TextView? = itemView.findViewById(R.id.audioPlayName)
        val artist: TextView? = itemView.findViewById(R.id.audioArtist)
        val menu: Button? = itemView.findViewById(R.id.audioMenu)
        val emptyAlbumPhoto: LinearLayout = itemView.findViewById(R.id.emptyAlbumPhoto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.audio_list_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        model.onBindAudioListViewHolder(this, holder, position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun getItemData(position: Int): Constants.Audio {
        return data[position]
    }
}