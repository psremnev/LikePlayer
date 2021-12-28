package com.Like

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.viewpager2.widget.ViewPager2

class AudioPlayFullscreen : DialogFragment() {
    private val model: Model by lazy { ViewModelProvider(activity as MainActivity).get() }
    private var playBtnChecked: Boolean = false;
    private val audioImageScrollList: ViewPager2? by lazy { view?.findViewById(R.id.audioImageScrollList)}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.audio_play_fullscreen, container, false)
    }

    override fun onStart() {
        super.onStart()

        setOrientationBaseLayout()
        rollBtnInit()
        imageScrollInit()
        infoInit()
        durationInit()
        seekbarInit()
        playBtnsInit()
    }

    private  fun setOrientationBaseLayout() {
        val baseLayout: LinearLayout? = view?.findViewById(R.id.baseLayout)
        if (activity?.resources?.configuration?.orientation === ActivityInfo.SCREEN_ORIENTATION_USER) {
            baseLayout?.orientation= LinearLayout.HORIZONTAL
        } else {
            baseLayout?.orientation = LinearLayout.VERTICAL
        }
    }

    private fun rollBtnInit() {
        val rollBtn: Button? = view?.findViewById(R.id.rollBtn)
        rollBtn?.setOnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commit()
        }
    }

    private fun seekbarInit() {
        val seekBar: SeekBar? = view?.findViewById(R.id.playSeekBar)
        seekBar?.max = model.audioPlayItemLiveData.value?.duration!!
        seekBar?.progress = model.mediaPlayer.currentPosition
        model.progress.observe(this, {
            seekBar?.progress = it
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

    private fun infoInit() {
        val name: TextView? = view?.findViewById(R.id.audioNameFullscr)
        val artist: TextView? = view?.findViewById(R.id.audioArtistFullscr)
        name?.text = model.audioPlayItemLiveData.value?.name
        artist?.text = model.audioPlayItemLiveData.value?.artist
        model.audioPlayItemLiveData.observe(this, {
            name?.text = it.name
            artist?.text = it.artist
        })
    }

    private fun durationInit() {
        val duration: TextView? = view?.findViewById(R.id.audioDurationFullscr)

        model.duration.observe(this, {
            duration?.text = it
        })
    }

    private fun imageScrollInit() {
        audioImageScrollList?.adapter = AudioViewPageAdapter(activity as MainActivity)
        audioImageScrollList?.currentItem = model.playItemPos
        audioImageScrollList?.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position !== model.playItemPos) {
                    val audioArray = model.audioLiveData.value
                    model.audioPlayItemLiveData.value = audioArray!![position]
                    model.playItemPos = position
                }
                super.onPageSelected(position)
            }
        })
    }

    private fun playBtnsInit() {
        val previousBtn: Button? = view?.findViewById(R.id.previousBtn)
        val playBtn: ImageButton? = view?.findViewById(R.id.playPauseBtn)
        val nextBtn: Button? = view?.findViewById(R.id.nextBtn)

        playBtnChecked = model.mediaPlayer.isPlaying
        if (playBtnChecked) {
            playBtn?.setImageResource(R.drawable.pause_fullscr)
        } else {
            playBtn?.setImageResource(R.drawable.play_fullscr)
        }
        playBtn?.setOnClickListener {
            playBtnChecked = !playBtnChecked
            if (playBtnChecked) {
                model.mediaPlayer.start()
                playBtn.setImageResource(R.drawable.pause_fullscr)
            } else {
                model.mediaPlayer.pause()
                playBtn.setImageResource(R.drawable.play_fullscr)
            }
        }
        nextBtn?.setOnClickListener {
            val newPos = model.playItemPos + 1
            if (newPos <=  model.audioLiveData.value?.size!! - 1) {
                model.playItemPos = newPos
                audioImageScrollList?.currentItem = newPos
                model.audioPlayItemLiveData.value = model.audioLiveData.value!![newPos]
            }
        }

        previousBtn?.setOnClickListener {
            val newPos = model.playItemPos - 1
            if (newPos >= 0) {
                model.playItemPos = newPos
                audioImageScrollList?.currentItem = newPos
                model.audioPlayItemLiveData.value = model.audioLiveData.value!![newPos]
            }
        }
    }

    /*private fun startNameScroll(): CountDownTimer {
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
    }*/

    /*private fun restartNameScroll() {
        nameScrollTimer?.cancel()
        nameScrollTimer?.start()
    }*/
}