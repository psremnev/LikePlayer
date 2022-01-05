package com.like.adapters

import android.content.ContentUris
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.like.*
import com.like.dataClass.Audio
import com.like.databinding.AudioListItemBinding
import com.like.selectAlbumDialog.SelectAlbumDialog
import java.io.FileNotFoundException
import javax.inject.Inject

class AudioListAdapter(val ctx: MainActivity, val data: ArrayList<Audio>):
    RecyclerView.Adapter<AudioListAdapter.ViewHolder>() {
    @Inject lateinit var model: MainActivityModel

    init {
        val mainActivityComponent = (ctx.application as App).mainActivityComponent
        mainActivityComponent?.inject(this)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: AudioListItemBinding? = DataBindingUtil.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AudioListItemBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.audio_list_item,
            parent,
            false
        )
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val position: Int = holder.adapterPosition
        val itemData = getItemData(position)
        holder.binding?.itemData = itemData
        holder.binding?.listeners = object: AudioListeners {
            override fun onAudioClick(view: View) {
                model.playItemPosition = position
                model.playItemDataObservable.onNext(itemData).run {
                    model.mediaPlayerStateChangedObservable.onNext(true)
                }
            }

            override fun onAudioMenuClick(view: View) {
                val args = Bundle()
                args.putInt("audioPosition", position)
                val frg = SelectAlbumDialog()
                frg.setStyle(DialogFragment.STYLE_NORMAL, R.style.ThemeOverlay_AppCompat_Dialog)
                frg.arguments = args
                frg.show(ctx.supportFragmentManager, "selectAlbumFragment")
            }

        }

        // Устанавливаем обложку записи
        val sArtworkUri = Uri.parse(Constants.ALBUM_ART_URI)
        val uri: Uri = ContentUris.withAppendedId(sArtworkUri, itemData.albumId)
        try {
            val inputStr = ctx.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStr)
            holder.binding?.emptyAlbumPhoto?.visibility = View.GONE
            holder.binding?.audioImage?.setImageBitmap(bitmap)
        } catch (e: FileNotFoundException) {
            holder.binding?.emptyAlbumPhoto?.visibility = View.VISIBLE
        }

        if (holder.binding?.audioImage?.drawable === null) {
            holder.binding?.emptyAlbumPhoto?.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private fun getItemData(position: Int): Audio {
        return data[position]
    }
}