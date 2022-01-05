package com.like.audioViewPage

import android.content.ContentUris
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.View
import androidx.lifecycle.ViewModel
import com.like.App
import com.like.Constants
import com.like.MainActivityModel
import rx.Subscription
import rx.subjects.PublishSubject
import java.io.FileNotFoundException
import javax.inject.Inject

class AudioViewPageModel: ViewModel() {
    lateinit var ctx: AudioViewPage
    @Inject lateinit var model: MainActivityModel
    val createViewPositionObservable: PublishSubject<Int> = PublishSubject.create()
    private var createViewPositionSubscription: Subscription? = null
    private var createViewPosition: Int = 0

    fun onCreateView(ctx: AudioViewPage) {
        this.ctx = ctx

        val mainActivityComponent = (ctx.activity?.application as App).mainActivityComponent
        mainActivityComponent?.inject(this)

        createViewPositionObservable.subscribe{
            createViewPosition = it
        }
        val sArtworkUri = Uri.parse(Constants.ALBUM_ART_URI)
        val uri: Uri = ContentUris.withAppendedId(sArtworkUri, model.audioData[createViewPosition].albumId)

        try {
            val inputStr = ctx.activity?.contentResolver?.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStr)
            ctx.binding.noPhoto.visibility = View.GONE
            ctx.binding.imageView.setImageBitmap(bitmap)
        } catch (e: FileNotFoundException) {
            ctx.binding.noPhoto.visibility = View.VISIBLE
        }
    }

    fun onDestroy() {
        if (createViewPositionSubscription != null) {
            createViewPositionSubscription?.unsubscribe()
        }
    }
}