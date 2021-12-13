package com.Like

import android.content.ContentUris
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import android.graphics.BitmapFactory
import java.io.FileNotFoundException

class AudioListAdapter(private val ctx: MainActivity):
    RecyclerView.Adapter<AudioListAdapter.ViewHolder>() {
    private val model = ViewModelProvider(ctx)[Model::class.java]

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
        val itemData = getItemData(position)
        val setName = {
            holder.name?.text = itemData.name
        }
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

        setName()
        setArtist()
        setPhoto()
        setListeners()
        if (holder.image?.drawable === null) {
            holder.emptyAlbumPhoto.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return model.audioLiveData.value!!.size
    }

    private fun getItemData(position: Int): Constants.Audio {
        return model.audioLiveData.value!![position]
    }
}