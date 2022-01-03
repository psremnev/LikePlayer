package com.like

object Constants {
    const val millisecondsInSec = 1000
    const val halfSecond = 500
    const val audioType: String = "audio/mpeg"
    const val AL_ALBUM_ID = 1
    const val unknownArtist = "<unknown>"
    const val ALBUM_ART_URI = "content://media/external/audio/albumart"
    interface Album {
        val id: Int?
        var name: String
        var audioCount: Int
    }

    interface Audio {
        val id: Int?
        val name: String
        val duration: Int
        val artist: String
        val url: String
        val albumId: Long
        var album: Int
    }

    interface AlbumAction {
        val action: String?
        val position: Int?
            get() = null
        val data: Any
    }
}