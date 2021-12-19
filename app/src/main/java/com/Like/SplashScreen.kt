package com.Like

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore

class SplashScreen : AppCompatActivity() {
    private val dataHelper: DataHelper by lazy { DataHelper(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
    }

    override fun onResume() {
        super.onResume()
        initNewMp3Songs()
        startActivity(Intent(this, MainActivity::class.java))
    }

    @SuppressLint("Range")
    fun initNewMp3Songs() {
        val allSongsUri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val cursor: Cursor? =
            contentResolver.query(allSongsUri, null, null, null, selection)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val fileType = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE))
                    if (fileType == Constants.audioType) {
                        val id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                        val audioCursor = dataHelper.getAudio(id)
                        if (audioCursor.count === 0) {
                            val baseName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                            dataHelper.addAudio(object : Constants.Audio {
                                override val id =
                                    cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                                override val name = baseName.replace(Regex("""[.com.mp3]*"""), "")
                                override val duration =
                                    cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                                override val artist =
                                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                                override val url =
                                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                                override val albumId: Long = cursor.getLong(cursor.getColumnIndex(
                                    MediaStore.Audio.Media.ALBUM_ID))
                                override var album = Constants.AL_ALBUM_ID
                            })
                        }
                    }
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
    }
}