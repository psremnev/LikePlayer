package com.Like

import android.media.MediaPlayer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class Model(): ViewModel() {
    var albumLiveData: MutableLiveData<ArrayList<Constants.Album>> = MutableLiveData<ArrayList<Constants.Album>>()
    var audioLiveData: MutableLiveData<ArrayList<Constants.Audio>> = MutableLiveData<ArrayList<Constants.Audio>>()
    var audioPlayItemLiveData: MutableLiveData<Constants.Audio> = MutableLiveData<Constants.Audio>()
    var dataHelper: DataHelper? = null;
    var mediaPlayer: MediaPlayer = MediaPlayer()
    var playItemPos: Int = 1
    var selectedAlbum: Int = 1
    var isAlbumChanged: Boolean = false
    var progress: MutableLiveData<Int> = MutableLiveData(0)
    var duration: MutableLiveData<String> = MutableLiveData("")
}