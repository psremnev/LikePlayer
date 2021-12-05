package com.Like

import androidx.lifecycle.MutableLiveData

object Constants {
    val millisecondsInSec = 1000
    val halfSecond = 500
    const val audioType: String = "audio/mpeg"
    const val AL_ALBUM_ID = 1
    const val FAVORITE_ALBUM_ID = 2
    const val unknownArtist = "<unknown>"
    const val ALBUM_ART_URI = "content://media/external/audio/albumart"
    interface Album {
        val id: Int?
        var name: String
        val audioCount: Int?
    }

    interface Audio {
        val id: Int?
        val name: String
        val duration: Int
        val artist: String
        val url: String
        val albumId: Long
        val album: Int
    }

    val albumLiveData: MutableLiveData<ArrayList<Album>> = MutableLiveData<ArrayList<Album>>()
    var audioLiveData: MutableLiveData<ArrayList<Audio>> = MutableLiveData<ArrayList<Audio>>()
    val audioPlayItemLiveData: MutableLiveData<Audio> = MutableLiveData<Audio>()
}