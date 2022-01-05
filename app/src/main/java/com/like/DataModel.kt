package com.like

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.like.dataClass.Album
import com.like.dataClass.Audio
import com.like.utils.DBHelper
import rx.schedulers.Schedulers
import rx.Observable

class DataModel(ctx: Context) {
    private val db: DBHelper = DBHelper(ctx)
    private val database: SQLiteDatabase = db.writableDatabase
    private val audioValues: ContentValues = ContentValues()
    private val albumValues: ContentValues = ContentValues()

    fun getAllAudioByAlbumIdObservable(albumId: Int): Observable<Audio> {
        return Observable.from(getAllAudioByAlbumId(albumId))
            .observeOn(Schedulers.newThread())
    }

    fun getAllAlbumObservable(): Observable<Album> {
        return Observable.from(getAllAlbum()).observeOn(Schedulers.newThread())
    }

    fun getAllAudioByAlbumId(albumId: Int): ArrayList<Audio> {
        return getAllAudioBySelection("album=$albumId")
    }

    fun getAllAudioBySearch(searchString: String): ArrayList<Audio> {
        return getAllAudioBySelection("name LIKE '%$searchString%'")
    }

    private fun getAllAudioBySelection(selection: String): ArrayList<Audio> {
        val audioData = ArrayList<Audio>()
        val cursor: Cursor =
            database.query(DBHelper.DATABASE_AUDIO_NAME, null, selection, null, null, null, null)
        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex(DBHelper.KEY_ID)
            val nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME)
            val durationIndex = cursor.getColumnIndex(DBHelper.KEY_DURATION)
            val artistIndex = cursor.getColumnIndex(DBHelper.KEY_ARTIST)
            val urlIndex = cursor.getColumnIndex(DBHelper.KEY_URL)
            val albumIndex = cursor.getColumnIndex(DBHelper.KEY_ALBUM)
            val albumUrlIndex = cursor.getColumnIndex(DBHelper.KEY_ALBUM_ID)
            do {
                audioData .add(Audio(
                        cursor.getInt(idIndex),
                        cursor.getString(nameIndex),
                        cursor.getInt(durationIndex),
                        cursor.getString(artistIndex),
                        cursor.getString(urlIndex),
                        cursor.getLong(albumUrlIndex),
                        cursor.getInt(albumIndex)
                ))
            } while (cursor.moveToNext())
        } else Log.d("DB", "Empty DB")
        cursor.close()
        return audioData
    }

    fun getAudio(id: Int): Cursor {
        return database.query(DBHelper.DATABASE_AUDIO_NAME, null, "${DBHelper.KEY_ID} = $id", null, null, null, null)
    }

    fun addAudio(data: Audio) {
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

    fun updateAudio(data: Audio) {
        audioValues.put(DBHelper.KEY_NAME, data.name)
        audioValues.put(DBHelper.KEY_DURATION, data.duration)
        audioValues.put(DBHelper.KEY_ARTIST, data.artist)
        audioValues.put(DBHelper.KEY_URL, data.url)
        audioValues.put(DBHelper.KEY_ALBUM, data.album)
        audioValues.put(DBHelper.KEY_ALBUM_ID, data.albumId)
        database.update(DBHelper.DATABASE_AUDIO_NAME, audioValues,"id=${data.id}", null);
        audioValues.clear()
    }

    fun getAlbum(id: Int?): Cursor {
        return database.query(DBHelper.DATABASE_ALBUM_NAME, null, "${DBHelper.KEY_ID} = $id", null, null, null, null)
    }

    fun getAlbumCount(id: Int): Int {
        val cursor: Cursor = database.query(DBHelper.DATABASE_AUDIO_NAME, null, "album=$id", null, null, null, null)
        return cursor.count
    }

    fun addAlbum(data: Album) {
        albumValues.put(DBHelper.KEY_ID, data.id)
        albumValues.put(DBHelper.KEY_NAME, data.name)
        albumValues.put(DBHelper.KEY_AUDIO_COUNT, data.audioCount)
        database.insert(DBHelper.DATABASE_ALBUM_NAME, null, albumValues);
        albumValues.clear()
    }

    fun updateAlbum(data: Album) {
        albumValues.put(DBHelper.KEY_NAME, data.name)
        albumValues.put(DBHelper.KEY_AUDIO_COUNT, data.audioCount)
        database.update(DBHelper.DATABASE_ALBUM_NAME, albumValues,"id=${data.id}", null);
        albumValues.clear()
    }

    fun deleteAlbum(id: Int?) {
        // меняем альбом для аудиозаписей на Все
        val albumAudio = getAllAudioByAlbumId(id!!)
        for (audio in albumAudio) {
            audio.album = Constants.AL_ALBUM_ID
            updateAudio(audio)
        }
        database.delete(DBHelper.DATABASE_ALBUM_NAME, "id=${id}", null);
    }

    fun getAllAlbum(): ArrayList<Album> {
        val albumsData = ArrayList<Album>()
        val cursor: Cursor =
            database.query(DBHelper.DATABASE_ALBUM_NAME, null, null, null, null, null, null)
        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex(DBHelper.KEY_ID)
            val nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME)
            do {
                albumsData.add(Album(
                        cursor.getInt(idIndex),
                        cursor.getString(nameIndex),
                        getAlbumCount(cursor.getInt(idIndex))
                ))
            } while (cursor.moveToNext())
        } else {
            Log.d("DB", "Empty DB")
        }
        cursor.close()
        return albumsData
    }

    fun initDefaultAlbum(ctx: Context) {
        val defaultAlbum = Album(Constants.AL_ALBUM_ID, ctx.getString(R.string.allAlbumName), 0)
        val albumCursor = getAlbum(Constants.AL_ALBUM_ID)
        if (albumCursor.count == 0) {
            addAlbum(defaultAlbum)
        }
    }
}