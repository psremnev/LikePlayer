package com.Like

import android.content.ContentUris
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.io.FileNotFoundException

class AudioViewPage(private val itemData: Constants.Audio): Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.audio_play_fullscreen_image_page, container, false)
        val image: ImageView? = view?.findViewById(R.id.imageView)
        val emptyText: TextView? = view?.findViewById(R.id.noPhoto)

        val sArtworkUri = Uri.parse(Constants.ALBUM_ART_URI)
        val uri: Uri = ContentUris.withAppendedId(sArtworkUri, itemData.albumId)

        try {
            val inputStr = activity?.contentResolver?.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStr)
            emptyText?.visibility = View.GONE
            image?.setImageBitmap(bitmap)
        } catch (e: FileNotFoundException) {
            emptyText?.visibility = View.VISIBLE
        }
        return view
    }
}