package com.like.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.like.*
import com.like.dataClass.Audio
import javax.inject.Inject

class AudioListAdapter(ctx: MainActivity, val data: ArrayList<Audio>):
    RecyclerView.Adapter<AudioListAdapter.ViewHolder>() {
    @Inject lateinit var model: MainActivityModel

    init {
        val mainActivityComponent = (ctx.application as App).mainActivityComponent
        mainActivityComponent?.inject(this)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val audioPlayContent: LinearLayout = itemView.findViewById(R.id.audioPlayContent)
        val image: ImageView? = itemView.findViewById(R.id.audioImage)
        val name: TextView? = itemView.findViewById(R.id.audioPlayName)
        val artist: TextView? = itemView.findViewById(R.id.audioArtist)
        val menu: ImageButton = itemView.findViewById(R.id.audioMenu)
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

    fun getItemData(position: Int): Audio {
        return data[position]
    }
}