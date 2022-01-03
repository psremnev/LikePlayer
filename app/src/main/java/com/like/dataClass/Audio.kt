package com.like.dataClass

data class Audio(
    val id: Int?,
    val name: String,
    val duration: Int,
    val artist: String,
    val url: String,
    val albumId: Long,
    var album: Int
)