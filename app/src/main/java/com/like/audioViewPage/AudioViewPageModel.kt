package com.like.audioViewPage

import android.content.ContentUris
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.like.Constants
import com.like.MainActivity
import com.like.MainActivityModel
import com.like.R
import java.io.FileNotFoundException

class AudioViewPageModel: ViewModel() {
    lateinit var ctx: AudioViewPage
    val model: MainActivityModel by lazy { ViewModelProvider(ctx.activity as MainActivity)[MainActivityModel::class.java] }

    fun onCreateView(ctx: AudioViewPage, inflater: LayoutInflater, container: ViewGroup?): View {
        this.ctx = ctx
        val view = inflater.inflate(R.layout.audio_play_fullscreen_image_page, container, false)
        val image: ImageView? = view?.findViewById(R.id.imageView)
        val emptyText: TextView? = view?.findViewById(R.id.noPhoto)

        val sArtworkUri = Uri.parse(Constants.ALBUM_ART_URI)
        val uri: Uri = ContentUris.withAppendedId(sArtworkUri, model.playItemData.albumId)

        try {
            val inputStr = ctx.activity?.contentResolver?.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStr)
            emptyText?.visibility = View.GONE
            image?.setImageBitmap(bitmap)
        } catch (e: FileNotFoundException) {
            emptyText?.visibility = View.VISIBLE
        }
        return view
    }
}