package com.Like

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView

class AudioListAdapter(private val ctx: MainActivity):
    RecyclerView.Adapter<AudioListAdapter.ViewHolder>() {
    private val model = ViewModelProvider(ctx)[Model::class.java]

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val audioPlayContent: LinearLayout = itemView.findViewById(R.id.audioPlayContent)
        val image: ImageView? = itemView.findViewById(R.id.audioImage)
        val name: TextView? = itemView.findViewById(R.id.audioName)
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
        val itemData = getItemData(position)
        val newName = itemData.name.replace(Regex("""[.com.mp3]*"""), "")
        holder.name?.text = newName
        if (itemData.artist != Constants.unknownArtist) {
            holder.artist?.text = itemData.artist
        } else {
            holder.artist?.text = ""
        }
        val uri = getImageUriByAlbumId(itemData.albumId)
        holder.image?.setImageURI(uri)
        // если нет превью у аудио
        if (holder.image?.drawable == null) {
            holder.emptyAlbumPhoto.visibility = View.VISIBLE
        }

        holder.audioPlayContent.setOnClickListener {
            model.audioPlayItemLiveData.value = itemData
            model.playItemPos = position
        }
        holder.menu?.setOnClickListener{
            val args = Bundle()
            args.putInt("audioPos", position)
            val frg = SelectAlbumDialog()
            frg.setStyle(DialogFragment.STYLE_NORMAL, R.style.ThemeOverlay_AppCompat_Dialog)
            frg.arguments = args
            frg.show(ctx.supportFragmentManager, "selectAlbum")
        }
    }

    override fun getItemCount(): Int {
        return model.audioLiveData.value!!.size
    }

    private fun getItemData(position: Int): Constants.Audio {
        return model.audioLiveData.value!![position]
    }

    private fun getImageUriByAlbumId(albumId: Long): Uri {
        val sArtworkUri = Uri.parse(Constants.ALBUM_ART_URI)
        return ContentUris.withAppendedId(sArtworkUri, albumId)
    }
}