package com.like.audioPlay

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.like.Constants
import com.like.MainActivity
import com.like.MainActivityModel
import com.like.R
import com.like.audioPlayFullscreen.AudioPlayFullscreen
import com.like.databinding.AudioPlayBinding
import java.text.SimpleDateFormat
import java.util.*

class AudioPlayModel: ViewModel() {
    lateinit var ctx: AudioPlay
    val model: MainActivityModel by lazy { ViewModelProvider(ctx.activity as MainActivity)[MainActivityModel::class.java] }
    val name: ObservableField<String> = ObservableField<String>("")
    val mediaPlayer: MediaPlayer = MediaPlayer()
    var progress: ObservableInt = ObservableInt(0)
    var progressMax: ObservableInt = ObservableInt(0)
    var duration: ObservableField<String> = ObservableField<String>("")

    private var isInit: Boolean = true
    private var fragmentVisibility: Boolean = false
    var playBtnChecked: Boolean = false
    var audioTimer: CountDownTimer? = null
    lateinit var itemData: Constants.Audio
    lateinit var binding: AudioPlayBinding

    fun  onCreateView(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.audio_play, container, false)
        binding.model = this
        fragmentVisibility = binding.root.visibility == View.VISIBLE
        return binding.root
    }

    private fun subscribeOnItemDataChange() {
        model.playItemDataObservable.subscribe {
            itemData = it
            if (!isInit) {
                name.set(itemData.name)
                duration.set(getTime(itemData.duration.toLong()))
                progressMax.set(itemData.duration)
                mediaPlayer.reset()
                initMediaPlayerData(itemData)
            }
            isInit = false
        }
    }

    fun onStart(ctx: AudioPlay) {
        this.ctx = ctx
        itemData = model.playItemData
        name.set(itemData.name)
        duration.set(getTime(itemData.duration.toLong()))
        progressMax.set(itemData.duration)
        subscribeOnItemDataChange()
        if (fragmentVisibility) {
            audioTimer = getTrackTimer()
            initPlayBtn()
            if (!mediaPlayer.isPlaying) {
                mediaPlayer.reset()
                initMediaPlayerData(itemData)
            }
        }
    }

    fun openFullscreen() {
        val audioPlayFullscreen= AudioPlayFullscreen()
        audioPlayFullscreen.setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        audioPlayFullscreen.show(ctx.activity!!.supportFragmentManager, "AudioPlayFullscreen")
    }

    private fun playAudio() {
        val playBtn = binding.audioPlay
        mediaPlayer.start()
        audioTimer?.start()
        playBtn.setImageResource(R.drawable.stop)
        playBtn.alpha = 0.7F
    }

    private fun initPlayBtn() {
        val playBtn = binding.audioPlay
        if (mediaPlayer.isPlaying) {
            playBtnChecked = mediaPlayer.isPlaying
            playBtn.setImageResource(R.drawable.stop)
            playBtn.alpha = 0.7F
            audioTimer?.start()
        } else {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(itemData.url)
        }
    }

    fun onPlayClick() {
        val playBtn = binding.audioPlay
        playBtnChecked = !playBtnChecked
        if (playBtnChecked) {
            playBtn.setImageResource(R.drawable.stop)
            playBtn.alpha = 0.7F
            mediaPlayer.start()
            audioTimer?.start()
        } else {
            playBtn.setImageResource(R.drawable.play)
            playBtn.alpha = 1F
            mediaPlayer.pause()
            audioTimer?.cancel()
        }
    }

    private fun initMediaPlayerData(itemData: Constants.Audio) {
        val audioAttributes = AudioAttributes.Builder()
        audioAttributes.setLegacyStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer.setDataSource(itemData.url)
        mediaPlayer.setAudioAttributes(audioAttributes.build())
        mediaPlayer.prepare()
    }

    @SuppressLint("SimpleDateFormat")
    private fun getTime(milliseconds: Long?): String {
        val formatter = SimpleDateFormat("mm:ss")
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = milliseconds!!.toLong()
        return formatter.format(calendar.time)
    }

    private fun getTrackTimer(): CountDownTimer {
        val timer = object: CountDownTimer(itemData.duration.toLong(), Constants.millisecondsInSec.toLong()) {
            override fun onTick(millisUntilFinished: Long) {
                val playerPos = mediaPlayer.currentPosition
                val newTime = itemData.duration - playerPos
                val newTextDuration = getTime(newTime.toLong())
                duration.set(newTextDuration)
                progress.set(playerPos)
            }

            override fun onFinish() {
                return
            }
        }
        return timer
    }
}