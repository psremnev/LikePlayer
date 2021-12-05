package com.Like

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context?): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        // создаем таблицу альбомов
        db.execSQL(
            "create table if not exists " +  DATABASE_ALBUM_NAME + " ("
                    + KEY_ID + " integer primary key autoincrement, "
                    + KEY_NAME + " text, "
                    + KEY_AUDIO_COUNT + " integer)"
        )
        // создаем таблицу аудио
        db.execSQL(
            "create table if not exists " +  DATABASE_AUDIO_NAME + " ("
                    + KEY_ID + " integer primary key autoincrement, "
                    + KEY_NAME + " text, "
                    + KEY_URL + " text,"
                    + KEY_DURATION + " integer, "
                    + KEY_ALBUM + " text, "
                    + KEY_ALBUM_ID + " text, "
                    + KEY_ARTIST + " text)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("drop table if exists $DATABASE_ALBUM_NAME")
        db.execSQL("drop table if exists $DATABASE_AUDIO_NAME")
        onCreate(db)
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "LikeDb"
        const val DATABASE_ALBUM_NAME = "ALBUM"
        const val DATABASE_AUDIO_NAME = "AUDIO"
        const val KEY_ID = "id"
        const val KEY_NAME = "name"
        const val KEY_URL = "url"
        const val KEY_ALBUM = "album"
        const val KEY_AUDIO_COUNT = "audio_count"
        const val KEY_DURATION = "duration"
        const val KEY_ARTIST = "artist"
        const val KEY_ALBUM_ID = "albumId"
    }
}