package com.like.audioPlayFullscreen

import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.like.Constants
import com.like.MainActivity
import com.like.MainActivityModel
import com.like.R
import com.like.adapters.AudioViewPageAdapter
import com.like.audioPlay.AudioPlayModel
import com.like.databinding.AudioPlayFullscreenBinding

class AudioPlayFullscreenModel: ViewModel() {

    lateinit var ctx: AudioPlayFullscreen
    val model: MainActivityModel by lazy { ViewModelProvider(ctx.activity as MainActivity)[MainActivityModel::class.java] }
    val audioPlayModel: AudioPlayModel by lazy { ViewModelProvider(ctx.activity as MainActivity)[AudioPlayModel::class.java] }

    lateinit var binding: AudioPlayFullscreenBinding
    private var playBtnChecked: Boolean = false;
    val imageScrollAdapter: AudioViewPageAdapter by lazy { AudioViewPageAdapter(ctx.activity as MainActivity) }
    val itemData: ObservableField<Constants.Audio> = ObservableField<Constants.Audio>()
    val duration: ObservableField<String> = ObservableField<String>("")
    val progressMax: ObservableInt = ObservableInt(0)
    val progress: ObservableInt = ObservableInt(0)
    val playBtn by lazy { binding.playPauseBtn }
    val audioImageScrollList by lazy { binding.audioImageScrollList }
    val mediaPlayer: MediaPlayer by lazy { audioPlayModel.mediaPlayer }

    fun onCreateView(ctx: AudioPlayFullscreen, inflater: LayoutInflater, container: ViewGroup?): View {
        this.ctx = ctx
        binding = DataBindingUtil.inflate(inflater,
            R.layout.audio_play_fullscreen, container, false)
        binding.model = this
        return binding.root
    }

    fun onStart() {
        setOrientationBaseLayout()
        imageScrollInit()
        seekbarInit()
        playBtnsInit()
    }

    private  fun setOrientationBaseLayout() {
        val baseLayout = binding.baseLayout
        if (ctx.resources.configuration?.orientation == ActivityInfo.SCREEN_ORIENTATION_USER) {
            baseLayout.orientation= LinearLayout.HORIZONTAL
        } else {
            baseLayout.orientation = LinearLayout.VERTICAL
        }
    }

    fun onRollBtnClick() {
        ctx.parentFragmentManager.beginTransaction().remove(ctx).commit()
    }

    private fun imageScrollInit() {
        audioImageScrollList.currentItem = 1
        audioImageScrollList.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position != model.playItemPosition) {
                    model.playItemDataObservable.onNext(model.audioData[position])
                    model.playItemPosition = position
                }
                super.onPageSelected(position)
            }
        })
    }

    private fun seekbarInit() {
        val seekBar: SeekBar = binding.playSeekBar
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress)
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

    private fun playBtnsInit() {
        playBtnChecked = mediaPlayer.isPlaying
        if (playBtnChecked) {
            playBtn.setImageResource(R.drawable.pause_fullscr)
        } else {
            playBtn.setImageResource(R.drawable.play_fullscr)
        }
    }

    fun onPlayClick() {
        playBtnChecked = !playBtnChecked
        if (playBtnChecked) {
            mediaPlayer.start()
            playBtn.setImageResource(R.drawable.pause_fullscr)
        } else {
            mediaPlayer.pause()
            playBtn.setImageResource(R.drawable.play_fullscr)
        }
    }

    fun onNextClick() {
        val newPos = model.playItemPosition + 1
        if (newPos <=  model.audioData.size - 1) {
            model.playItemPosition = newPos
            audioImageScrollList.currentItem = newPos
            model.playItemDataObservable.onNext(model.audioData[newPos])
        }
    }

    fun onPreviousClick() {
        val newPos = model.playItemPosition - 1
        if (newPos >= 0) {
            model.playItemPosition = newPos
            audioImageScrollList.currentItem = newPos
            model.playItemDataObservable.onNext(model.audioData[newPos])
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