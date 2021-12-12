package com.Like

import android.content.res.Resources
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
import androidx.core.view.isVisible
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
    var fragmentVisibility: Boolean = false;
    var name: TextView? = null
    var duration: TextView? = null
    var playBtn: ToggleButton? = null
    var nameScroll: HorizontalScrollView? = null
    var audioTimer: CountDownTimer? = null
    private var itemData: MutableLiveData<Constants.Audio>? = null
    private val mediaPlayer: MediaPlayer by lazy { model?.mediaPlayer!! }
    private var audioData: MutableLiveData<ArrayList<Constants.Audio>>? = null
    private var nameScrollTimer: CountDownTimer? = null

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
            progress = view?.findViewById(R.id.audioPlayProgress)
            name = view?.findViewById(R.id.audioName)
            duration = view?.findViewById(R.id.audioPlayDuration)

            name?.text = itemData?.value?.name
            duration?.text = getTime(itemData?.value?.duration?.toLong())
            progress?.max = itemData?.value?.duration!!

            nameScroll = view?.findViewById(R.id.nameScroll)
            name?.setOnClickListener {
                val fullscrFrg = AudioPlayFullscreen()
                fullscrFrg.setStyle(STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar_Fullscreen)
                fullscrFrg.show(activity?.supportFragmentManager!!, "AudioPlayFullscr")
            }
            audioTimer = getTrackTimer()
            initPlayBtn()
            nameScrollTimer = startNameScroll()
            itemData?.observe(viewLifecycleOwner, {
                if (!isInit) {
                    name?.text = itemData?.value?.name
                    duration?.text = getTime(itemData?.value?.duration?.toLong())
                    progress?.max = itemData?.value?.duration!!
                    mediaPlayer.reset()
                    initMediaPlayerData(itemData!!)
                    mediaPlayer.start()
                    audioTimer?.start()
                    playBtn?.setButtonDrawable(R.drawable.stop)
                    restartNameScroll()
                }
                isInit = false
            })
        }
    }

    private fun initPlayBtn() {
        playBtn = view?.findViewById(R.id.audioPlayPause)
        if (mediaPlayer.isPlaying) {
            playBtn?.isChecked = mediaPlayer.isPlaying
            playBtn?.setButtonDrawable(R.drawable.stop)
            audioTimer?.start()
            startNameScroll()
        } else {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(itemData?.value?.url)
        }
        playBtn?.setOnClickListener {
            if (playBtn?.isChecked == true) {
                playBtn?.setButtonDrawable(R.drawable.stop)
                //playBtn?.setBackgroundResource(R.drawable.stop)
                mediaPlayer.start()
                audioTimer?.start()
            } else {
                playBtn?.setButtonDrawable(R.drawable.play)
                mediaPlayer.pause()
                audioTimer?.cancel()
            }
        }
    }

    private fun restartNameScroll() {
        nameScrollTimer?.cancel()
        nameScrollTimer?.start()
    }

    private fun initMediaPlayerData(itemData: MutableLiveData<Constants.Audio>) {
        val audioAttributes = AudioAttributes.Builder()
        audioAttributes.setLegacyStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer.setDataSource(itemData?.value?.url)
        mediaPlayer.setAudioAttributes(audioAttributes.build())
        mediaPlayer.prepare()
    }

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

    private fun startNameScroll(): CountDownTimer {
        val timer = object: CountDownTimer(itemData?.value?.duration!!.toLong(), Constants.halfSecond.toLong()) {
            override fun onTick(millisUntilFinished: Long) {
                val currentScrollX = nameScroll?.scrollX!!
                val textLength = name?.right!!
                val scrollXPos = nameScroll?.width!! + nameScroll?.scrollX!!
                if (scrollXPos < textLength) {
                    nameScroll?.scrollX = currentScrollX + 5
                } else {
                    nameScroll?.smoothScrollTo(0, 0)
                }
            }
            override fun onFinish() {
                start()
            }
        }
        timer.start()
        return timer
    }
}