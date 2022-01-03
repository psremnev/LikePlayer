package com.like.audioViewPage

import android.content.ContentUris
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.like.Constants
import com.like.MainActivity
import com.like.MainActivityModel
import java.io.FileNotFoundException

class AudioViewPageModel: ViewModel() {
    lateinit var ctx: AudioViewPage
    val model: MainActivityModel by lazy { ViewModelProvider(ctx.activity as MainActivity)[MainActivityModel::class.java] }

    fun onCreateView(ctx: AudioViewPage) {
        this.ctx = ctx
        val sArtworkUri = Uri.parse(Constants.ALBUM_ART_URI)
        val uri: Uri = ContentUris.withAppendedId(sArtworkUri, model.audioData[ctx.position].albumId)

        try {
            val inputStr = ctx.activity?.contentResolver?.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStr)
            ctx.binding.noPhoto.visibility = View.GONE
            ctx.binding.imageView.setImageBitmap(bitmap)
        } catch (e: FileNotFoundException) {
            ctx.binding.noPhoto.visibility = View.VISIBLE
        }
    }
}