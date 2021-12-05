package com.Like

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log

class DataHelper(ctx: Context) {
    private val db: DBHelper = DBHelper(ctx)
    private val database: SQLiteDatabase = db.writableDatabase
    private val audioValues: ContentValues = ContentValues()
    private val albumValues: ContentValues = ContentValues()

    fun getAllAudioByAlbumId(albumId: Int): ArrayList<Constants.Audio> {
        val audioData = ArrayList<Constants.Audio>()
        val cursor: Cursor =
            database.query(DBHelper.DATABASE_AUDIO_NAME, null, "album=$albumId", null, null, null, null)
        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex(DBHelper.KEY_ID)
            val nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME)
            val durationIndex = cursor.getColumnIndex(DBHelper.KEY_DURATION)
            val artistIndex = cursor.getColumnIndex(DBHelper.KEY_ARTIST)
            val urlIndex = cursor.getColumnIndex(DBHelper.KEY_URL)
            val albumIndex = cursor.getColumnIndex(DBHelper.KEY_ALBUM)
            val albumUrlIndex = cursor.getColumnIndex(DBHelper.KEY_ALBUM_ID)
            do {
                audioData .add(
                    object: Constants.Audio {
                        override val id = cursor.getInt(idIndex)
                        override var name = cursor.getString(nameIndex)
                        override val duration = cursor.getInt(durationIndex)
                        override val artist = cursor.getString(artistIndex)
                        override val url = cursor.getString(urlIndex)
                        override val albumId = cursor.getLong(albumUrlIndex)
                        override val album = cursor.getInt(albumIndex)
                    }
                );
            } while (cursor.moveToNext())
        } else Log.d("DB", "Empty DB")
        cursor.close()
        return audioData
    }

    fun getAudio(id: Int): Cursor {
        return database.query(DBHelper.DATABASE_AUDIO_NAME, null, "${DBHelper.KEY_ID} = $id", null, null, null, null)
    }

    fun deleteAllAudio() {
        database.delete(DBHelper.DATABASE_AUDIO_NAME, null, null);

    }

    fun addAudio(data: Constants.Audio) {
        audioValues.put(DBHelper.KEY_ID, data.id)
        audioValues.put(DBHelper.KEY_NAME, data.name)
        audioValues.put(DBHelper.KEY_DURATION, data.duration)
        audioValues.put(DBHelper.KEY_ARTIST, data.artist)
        audioValues.put(DBHelper.KEY_URL, data.url)
        audioValues.put(DBHelper.KEY_ALBUM, data.album)
        audioValues.put(DBHelper.KEY_ALBUM_ID, data.albumId)
        database.insert(DBHelper.DATABASE_AUDIO_NAME, null, audioValues);
        audioValues.clear()
    }

    fun updateAudio(data: Constants.Audio) {
        audioValues.put(DBHelper.KEY_NAME, data.name)
        audioValues.put(DBHelper.KEY_DURATION, data.duration)
        audioValues.put(DBHelper.KEY_ARTIST, data.artist)
        audioValues.put(DBHelper.KEY_URL, data.url)
        audioValues.put(DBHelper.KEY_ALBUM, data.album)
        audioValues.put(DBHelper.KEY_ALBUM_ID, data.albumId)
        database.update(DBHelper.DATABASE_AUDIO_NAME, audioValues,"id=${data.id}", null);
        audioValues.clear()
    }

    fun deleteAudio(id: String) {
        database.delete(DBHelper.DATABASE_AUDIO_NAME, "id=${id}", null);
    }

    fun getAlbum(id: Int?): Cursor {
        return database.query(DBHelper.DATABASE_ALBUM_NAME, null, "${DBHelper.KEY_ID} = $id", null, null, null, null)
    }

    fun getAlbumCount(id: Int): Int {
        val cursor: Cursor = database.query(DBHelper.DATABASE_AUDIO_NAME, null, "album=$id", null, null, null, null)
        return cursor.count
    }

    fun addAlbum(data: Constants.Album) {
        albumValues.put(DBHelper.KEY_NAME, data.name)
        albumValues.put(DBHelper.KEY_AUDIO_COUNT, data.audioCount)
        database.insert(DBHelper.DATABASE_ALBUM_NAME, null, albumValues);
        albumValues.clear()
    }

    fun updateAlbum(data: Constants.Album) {
        albumValues.put(DBHelper.KEY_NAME, data.name)
        albumValues.put(DBHelper.KEY_AUDIO_COUNT, data.audioCount)
        database.update(DBHelper.DATABASE_ALBUM_NAME, albumValues,"id=${data.id}", null);
        albumValues.clear()
    }

    fun deleteAlbum(id: Int?) {
        database.delete(DBHelper.DATABASE_ALBUM_NAME, "id=${id}", null);
    }

    fun getAllAlbum(): ArrayList<Constants.Album> {
        val albumsData = ArrayList<Constants.Album>()
        val cursor: Cursor =
            database.query(DBHelper.DATABASE_ALBUM_NAME, null, null, null, null, null, null)
        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex(DBHelper.KEY_ID)
            val nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME)
            val audioCountIndex = cursor.getColumnIndex(DBHelper.KEY_AUDIO_COUNT)
            do {
                albumsData.add(
                    object: Constants.Album {
                        override val id = cursor.getInt(idIndex)
                        override var name = cursor.getString(nameIndex)
                        override val audioCount = getAlbumCount(id)
                    }
                );
            } while (cursor.moveToNext())
        } else Log.d("DB", "Empty DB")
        cursor.close()
        return albumsData
    }

    fun initDefaultAlbum(ctx: Context) {
        val defaultAlbums: ArrayList<Constants.Album> = arrayListOf(
            object: Constants.Album {
                override val id = Constants.AL_ALBUM_ID
                override var name = ctx.getString(R.string.allAlbumName);
                override val audioCount = 0
            },
            object: Constants.Album {
                override val id = Constants.FAVORITE_ALBUM_ID
                override var name = ctx.getString(R.string.favoriteAlbumName);
                override val audioCount = 0
            }
        )

        for (album in defaultAlbums) {
            val albumCursor = getAlbum(album.id)
            if (albumCursor.count === 0) {
                addAlbum(album)
            }
        }
    }
}