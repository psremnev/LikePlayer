package com.Like

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment.STYLE_NORMAL
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AudioPlay : Fragment() {
    private var model: Model? = null
    private var isInit: Boolean = true
    private var progress: ProgressBar? = null
    var fragmentVisibility: Boolean = false
    var name: TextView? = null
    var duration: TextView? = null
    var playBtn: ImageButton? = null
    var playBtnChecked: Boolean = false
    var audioTimer: CountDownTimer? = null
    private var itemData: MutableLiveData<Constants.Audio>? = null
    private val mediaPlayer: MediaPlayer by lazy { model?.mediaPlayer!! }
    private var audioData: MutableLiveData<ArrayList<Constants.Audio>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProvider(activity as MainActivity).get()
        itemData = model?.audioPlayItemLiveData
        audioData = model?.audioLiveData
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentVisibility = container?.visibility === View.VISIBLE
        return inflater.inflate(R.layout.audio_play, container, false)
    }

    override fun onStart() {
        super.onStart()
        if (fragmentVisibility) {
            if (isInit) {
                val container: LinearLayout? = view?.findViewById(R.id.mainContent)
                val openFullscreen = {
                    val fullscrFrg = AudioPlayFullscreen()
                    fullscrFrg.setStyle(STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar_Fullscreen)
                    fullscrFrg.show(activity?.supportFragmentManager!!, "AudioPlayFullscr")
                }
                progress = view?.findViewById(R.id.audioPlayProgress)
                name = view?.findViewById(R.id.audioPlayName)
                duration = view?.findViewById(R.id.audioPlayDuration)

                name?.text = itemData?.value?.name
                duration?.text = getTime(itemData?.value?.duration?.toLong())
                progress?.max = itemData?.value?.duration!!

                container?.setOnClickListener {
                    openFullscreen()
                }
                name?.setOnClickListener {
                    openFullscreen()
                }
                audioTimer = getTrackTimer()
                initPlayBtn()
                mediaPlayer.reset()
                initMediaPlayerData(itemData!!)
            }
            itemData?.observe(viewLifecycleOwner, {
                if (!isInit) {
                    name?.text = itemData?.value?.name
                    duration?.text = getTime(itemData?.value?.duration?.toLong())
                    progress?.max = itemData?.value?.duration!!
                    mediaPlayer.reset()
                    initMediaPlayerData(itemData!!)
                    if (!model?.isAlbumChanged!!) {
                        playAudio()
                    } else {
                        playBtn?.setImageResource(R.drawable.play)
                        progress?.progress = 0
                    }
                }
                isInit = false
                model?.isAlbumChanged = false
            })
        }
    }

    private fun playAudio() {
        mediaPlayer.start()
        audioTimer?.start()
        playBtn?.setImageResource(R.drawable.stop)
    }

    private fun initPlayBtn() {
        playBtn = view?.findViewById(R.id.audioPlay)
        if (mediaPlayer.isPlaying) {
            playBtnChecked = mediaPlayer.isPlaying
            playBtn?.setImageResource(R.drawable.stop)
            audioTimer?.start()
        } else {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(itemData?.value?.url)
        }
        playBtn?.setOnClickListener {
            playBtnChecked = !playBtnChecked
            if (playBtnChecked) {
                playBtn?.setImageResource(R.drawable.stop)
                mediaPlayer.start()
                audioTimer?.start()
            } else {
                playBtn?.setImageResource(R.drawable.play)
                mediaPlayer.pause()
                audioTimer?.cancel()
            }
        }
    }

    private fun initMediaPlayerData(itemData: MutableLiveData<Constants.Audio>) {
        val audioAttributes = AudioAttributes.Builder()
        audioAttributes.setLegacyStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer.setDataSource(itemData.value?.url)
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
        val timer = object: CountDownTimer(itemData?.value?.duration!!.toLong(), Constants.millisecondsInSec.toLong()) {
            override fun onTick(millisUntilFinished: Long) {
                val time = mediaPlayer.currentPosition
                val newTime = itemData?.value?.duration!! - time
                val newTextDuration = getTime(newTime.toLong())
                model?.duration?.value = newTextDuration
                model?.progress?.value = time
                progress?.progress = time
                duration?.text = newTextDuration
            }

            override fun onFinish() {
                return
            }
        }
        return timer
    }
}