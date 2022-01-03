package com.like

interface Interfaces {
    interface AlbumAction {
        val action: String?
        val position: Int?
            get() = null
        val data: Any
    }
}