package com.Like

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.ToggleButton
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.RecyclerView

class AudioPlayFullscreen : DialogFragment() {
    private val model: Model by lazy { ViewModelProvider(activity as MainActivity).get() }
    private var itemData: MutableLiveData<Constants.Audio>? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        itemData = model.audioPlayItemLiveData
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.audio_play_fullscreen, container, false)
    }

    override fun onStart() {
        super.onStart()

        val rollBtn: Button? = view?.findViewById(R.id.rollBtn)
        rollBtn?.setOnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commit()
        }
        val name: TextView? = view?.findViewById(R.id.audioNameFullscr)
        val artist: TextView? = view?.findViewById(R.id.audioArtistFullscr)
        val seekBar: SeekBar? = view?.findViewById(R.id.playSeekBar)
        val previousBtn: Button? = view?.findViewById(R.id.previousBtn)
        val playBtn: ToggleButton? = view?.findViewById(R.id.playPauseBtn)
        val nextBtn: Button? = view?.findViewById(R.id.nextBtn)
        val duration: TextView? = view?.findViewById(R.id.audioDurationFullscr)
        val audioImageScrollList: RecyclerView? = view?.findViewById(R.id.audioImageScrollList)

        name?.text = itemData?.value?.name
        artist?.text = itemData?.value?.artist

        seekBar?.max = itemData?.value?.duration!!
        seekBar?.progress = model.mediaPlayer.currentPosition

        playBtn?.isChecked = model.mediaPlayer.isPlaying
        if (playBtn!!.isChecked) {
            playBtn.setButtonDrawable(R.drawable.stop)
        } else {
            playBtn.setButtonDrawable(R.drawable.play)
        }
        playBtn?.setOnClickListener {
            if (playBtn.isChecked) {
                model.mediaPlayer.start()
                playBtn.setButtonDrawable(R.drawable.stop)
            } else {
                model.mediaPlayer.pause()
                playBtn.setButtonDrawable(R.drawable.play)
            }
        }

        nextBtn?.setOnClickListener {
            val newPos = model.playItemPos + 1
            model.playItemPos = newPos
            model.audioPlayItemLiveData.value = model.audioLiveData.value!![newPos]
        }

        previousBtn?.setOnClickListener {
            val newPos = model.playItemPos - 1
            model.playItemPos = newPos
            model.audioPlayItemLiveData.value = model.audioLiveData.value!![newPos]
        }

        model.progress.observe(this, {
            seekBar?.progress = it
        })

        model.duration.observe(this, {
            duration?.text = it
        })

        itemData!!.observe(this, {
            itemData = MutableLiveData(it)
            name?.text = it.name
            artist?.text = it.artist
        })

        seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    model.mediaPlayer.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                return
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                return
            }
        })
    }
}