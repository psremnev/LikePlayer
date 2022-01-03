package com.like.audioPlayFullscreen

import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.os.CountDownTimer
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

class AudioPlayFullscreenModel: ViewModel() {

    lateinit var ctx: AudioPlayFullscreen
    val model: MainActivityModel by lazy { ViewModelProvider(ctx.activity as MainActivity)[MainActivityModel::class.java] }
    private val audioPlayModel: AudioPlayModel by lazy { ViewModelProvider(ctx.activity as MainActivity)[AudioPlayModel::class.java] }

    private var playBtnChecked: Boolean = false;
    private lateinit var nameScrollTimer: CountDownTimer
    val itemData: ObservableField<Constants.Audio> = ObservableField<Constants.Audio>()
    val duration: ObservableField<String> = ObservableField<String>("")
    val progressMax: ObservableInt = ObservableInt(0)
    val progress: ObservableInt = ObservableInt(0)
    val mediaPlayer: MediaPlayer by lazy { ctx.mediaPlayer }

    fun onCreateView(ctx: AudioPlayFullscreen) {
        this.ctx = ctx
        itemData.set(model.playItemData)
        duration.set(audioPlayModel.duration.get())
        ctx.binding.model = this
    }

    fun onStart() {
        initNameScrollTimer()
        setOrientationBaseLayout()
        imageScrollInit()
        seekbarInit()
        playBtnsInit()
        subscribeOnDataChange()
    }

    private fun subscribeOnDataChange() {
        audioPlayModel.progressObservable.subscribe {
            progress.set(it)
        }
        audioPlayModel.durationObservable.subscribe {
            duration.set(it)
        }
    }

    private  fun setOrientationBaseLayout() {
        val baseLayout = ctx.binding.baseLayout
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
        ctx.binding.audioImageScrollList.adapter = AudioViewPageAdapter(ctx.activity as MainActivity, model.audioData)
        ctx.binding.audioImageScrollList.setCurrentItem(model.playItemPosition, true)
        ctx.binding.audioImageScrollList.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position != model.playItemPosition) {
                    val newItemData = model.audioData[position]
                    itemData.set(newItemData)
                    model.playItemPosition = position
                    model.playItemDataObservable.onNext(newItemData).run {
                        mediaPlayer.start()
                        ctx.binding.playPauseBtn.setImageResource(R.drawable.pause_fullscr)
                    }
                }
                super.onPageSelected(position)
            }
        })
    }

    private fun seekbarInit() {
        val seekBar: SeekBar = ctx.binding.playSeekBar
        progressMax.set(itemData.get()!!.duration)
        progress.set(mediaPlayer.currentPosition)
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
            ctx.binding.playPauseBtn.setImageResource(R.drawable.pause_fullscr)
            nameScrollTimer.start()
        } else {
            ctx.binding.playPauseBtn.setImageResource(R.drawable.play_fullscr)
        }
    }

    fun onPlayClick() {
        playBtnChecked = !playBtnChecked
        if (playBtnChecked) {
            mediaPlayer.start()
            ctx.binding.playPauseBtn.setImageResource(R.drawable.pause_fullscr)
            audioPlayModel.audioTimer?.start()
        } else {
            mediaPlayer.pause()
            ctx.binding.playPauseBtn.setImageResource(R.drawable.play_fullscr)
            audioPlayModel.audioTimer?.cancel()
        }
    }

    fun onNextClick() {
        restartNameScroll()
        val newPos = model.playItemPosition + 1
        if (newPos <=  model.audioData.size - 1) {
            val newItemData = model.audioData[newPos]
            model.playItemPosition = newPos
            ctx.binding.audioImageScrollList.currentItem = newPos
            itemData.set(newItemData)
            model.playItemDataObservable.onNext(newItemData).run {
                mediaPlayer.start()
                ctx.binding.playPauseBtn.setImageResource(R.drawable.pause_fullscr)
            }
        }
    }

    fun onPreviousClick() {
        restartNameScroll()
        val newPos = model.playItemPosition - 1
        if (newPos >= 0) {
            val newItemData = model.audioData[newPos]
            model.playItemPosition = newPos
            ctx.binding.audioImageScrollList.currentItem = newPos
            itemData.set(newItemData)
            model.playItemDataObservable.onNext(newItemData).run {
                mediaPlayer.start()
                ctx.binding.playPauseBtn.setImageResource(R.drawable.pause_fullscr)
            }
        }
    }

    private fun initNameScrollTimer() {
        nameScrollTimer = object: CountDownTimer(itemData.get()?.duration!!.toLong(), Constants.halfSecond.toLong()) {
            override fun onTick(millisUntilFinished: Long) {
                val currentScrollX = ctx.binding.nameScrollFullscr.scrollX
                val textLength = ctx.binding.audioNameFullscr.right
                val scrollXPos = ctx.binding.nameScrollFullscr.width + ctx.binding.nameScrollFullscr.scrollX
                if (scrollXPos < textLength) {
                    ctx.binding.nameScrollFullscr.scrollX = currentScrollX + 5
                } else {
                    ctx.binding.nameScrollFullscr.smoothScrollTo(0, 0)
                }
            }
            override fun onFinish() {
                start()
            }
        }
    }

    private fun restartNameScroll() {
        nameScrollTimer.cancel()
        nameScrollTimer.start()
    }
}