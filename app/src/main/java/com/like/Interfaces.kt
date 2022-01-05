package com.like

import android.view.View

interface AlbumAction {
    val action: String?
    val position: Int?
        get() = null
    val data: Any
}

interface AlbumListeners {
    fun onClick(view: View)
    fun onLongTapClick(view: View): Boolean
}

interface AudioListeners {
    fun onAudioClick(view: View)
    fun onAudioMenuClick(view: View)
}