package com.Like


import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class Model(ctx: Context): ViewModel() {
    private var dataHelper: DataHelper? = DataHelper(ctx)
    var albumLiveData: MutableLiveData<ArrayList<Constants.Album>> = MutableLiveData<ArrayList<Constants.Album>>()
    var audioLiveData: MutableLiveData<ArrayList<Constants.Audio>> = MutableLiveData<ArrayList<Constants.Audio>>()
    var audioPlayItemLiveData: MutableLiveData<Constants.Audio> = MutableLiveData<Constants.Audio>()

    init {
        initAudioData()
        initAlbumData()
        initAudioPlayItemData()
    }


    fun initAudioData() {
        audioLiveData.value = dataHelper?.getAllAudioByAlbumId(Constants.AL_ALBUM_ID)
    }

    fun initAlbumData() {
        albumLiveData.value = dataHelper?.getAllAlbum()
    }

    fun initAudioPlayItemData() {
        val audioData = audioLiveData.value
        if (audioData !== null && audioData?.size !== 0) {
            audioPlayItemLiveData.value = audioData[0]
        }
    }

    fun getAudioData(): MutableLiveData<ArrayList<Constants.Audio>> {
        return  audioLiveData
    }

    fun getAlbumData(): MutableLiveData<ArrayList<Constants.Album>> {
        return albumLiveData
    }

    fun getAudioPlayItemData(): MutableLiveData<Constants.Audio> {
        return audioPlayItemLiveData
    }

    fun setAudioPlayData(value: Constants.Audio) {
        audioPlayItemLiveData.value = value
    }
}