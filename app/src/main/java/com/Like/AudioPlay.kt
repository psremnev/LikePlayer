package com.Like

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AudioPlay : Fragment() {
    private var model: Model? = null
    private var isInit: Boolean = true;
    private var progress: ProgressBar? = null
    var name: TextView? = null
    var duration: TextView? = null
    var playBtn: ToggleButton? = null
    var nameScroll: HorizontalScrollView? = null
    var audioTimer: CountDownTimer? = null
    private var itemData: MutableLiveData<Constants.Audio>? = null
    private val mediaPlayer: MediaPlayer = MediaPlayer()
    private var audioData: MutableLiveData<ArrayList<Constants.Audio>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val callActivity = activity as MainActivity
        model = callActivity.getModel()
        itemData = model?.getAudioPlayItemData()
        audioData = model?.getAudioData()
        return inflater.inflate(R.layout.audio_play, container, false)
    }

    override fun onStart() {
        super.onStart()

        if (view?.isVisible === true) {
            initPlayBtn()
            progress = view?.findViewById(R.id.audioPlayProgress)
            name = view?.findViewById(R.id.audioName)
            duration = view?.findViewById(R.id.audioPlayDuration)
            nameScroll = view?.findViewById(R.id.nameScroll)
            audioTimer = getTrackTimer()
            val act: MainActivity = activity as MainActivity
            val b = act.getModel()
            itemData?.observe(viewLifecycleOwner, {
                mediaPlayer.reset()

                initMediaPlayerData(itemData!!)
                if (!isInit) {
                    mediaPlayer.start()
                    audioTimer?.start()
                    playBtn?.setBackgroundResource(R.drawable.stop)
                }
                isInit = false
            })
            startNameScroll()
        }
    }

    private fun initPlayBtn() {
        playBtn = view?.findViewById(R.id.audioPlayPause)
        mediaPlayer.setDataSource(itemData?.value?.url)
        playBtn?.setOnClickListener {
            if (playBtn?.isChecked == true) {
                playBtn?.setBackgroundResource(R.drawable.stop)
                mediaPlayer.start()
                audioTimer?.start()
            } else {
                playBtn?.setBackgroundResource(R.drawable.play)
                mediaPlayer.pause()
                audioTimer?.cancel()
            }
        }
    }

    private fun initMediaPlayerData(itemData: MutableLiveData<Constants.Audio>) {
        name?.text = itemData.value?.name
        duration?.text = getTime(itemData.value?.duration?.toLong())
        progress?.max = itemData.value?.duration!!
        val audioAttributes = AudioAttributes.Builder()
        audioAttributes.setLegacyStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer.setDataSource(requireContext(), Uri.parse(itemData.value?.url))
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
                progress?.progress = time
                duration?.text = getTime(newTime.toLong())
            }

            override fun onFinish() {
                return
            }
        }
        return timer
    }

    private fun startNameScroll() {
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
    }
}