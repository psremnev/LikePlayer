package com.like.audioPlay

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.view.View
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
import rx.subjects.PublishSubject
import java.text.SimpleDateFormat
import java.util.*

class AudioPlayModel: ViewModel() {
    lateinit var ctx: AudioPlay
    val model: MainActivityModel by lazy { ViewModelProvider(ctx.activity as MainActivity)[MainActivityModel::class.java] }
    val name: ObservableField<String> = ObservableField<String>("")
    val mediaPlayer: MediaPlayer by lazy { ctx.mediaPlayer }
    var progress: ObservableInt = ObservableInt(0)
    val progressObservable: PublishSubject<Int> = PublishSubject.create()
    val durationObservable: PublishSubject<String> = PublishSubject.create()
    var progressMax: ObservableInt = ObservableInt(0)
    var duration: ObservableField<String> = ObservableField<String>("")
    private var fragmentVisibility: Boolean = false
    var playBtnChecked: Boolean = false
    var audioTimer: CountDownTimer? = null
    lateinit var itemData: Constants.Audio


    fun  onCreateView(ctx: AudioPlay) {
        ctx.binding.model = this
        fragmentVisibility = ctx.binding.root.visibility == View.VISIBLE
    }

    private fun subscribeOnItemDataChange() {
        model.playItemDataObservable.subscribe {
            itemData = it
            name.set(itemData.name)
            duration.set(getTime(itemData.duration.toLong()))
            progressMax.set(itemData.duration)
            initMediaPlayerData(itemData)
        }

        model.mediaPlayerStateChangedObservable.subscribe {
            playAudio()
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
        val playBtn = ctx.binding.audioPlay
        mediaPlayer.start()
        audioTimer?.start()
        playBtn.setImageResource(R.drawable.stop)
        playBtn.alpha = 0.7F
    }

    private fun initPlayBtn() {
        val playBtn = ctx.binding.audioPlay
        if (mediaPlayer.isPlaying) {
            playBtnChecked = mediaPlayer.isPlaying
            playBtn.setImageResource(R.drawable.stop)
            playBtn.alpha = 0.7F
            audioTimer?.start()
        } else {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(itemData.url)
            audioTimer?.cancel()
        }
    }

    fun onPlayClick() {
        val playBtn = ctx.binding.audioPlay
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
        mediaPlayer.reset()
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
                durationObservable.onNext(newTextDuration)
                progressObservable.onNext(playerPos)
            }

            override fun onFinish() {
                return
            }
        }
        return timer
    }
}