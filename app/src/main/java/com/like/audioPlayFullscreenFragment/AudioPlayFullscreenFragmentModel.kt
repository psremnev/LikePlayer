package com.like.audioPlayFullscreenFragment

import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.widget.*
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel
import androidx.viewpager2.widget.ViewPager2
import com.like.*
import com.like.adapters.AudioViewPageAdapter
import com.like.audioPlayFragment.AudioPlayFragmentModel
import com.like.dataClass.Audio
import rx.Subscription
import rx.schedulers.Schedulers
import javax.inject.Inject

class AudioPlayFullscreenFragmentModel: ViewModel() {

    lateinit var ctx: AudioPlayFullscreenFragment
    @Inject lateinit var model: MainActivityModel
    @Inject lateinit var audioPlayFragmentModel: AudioPlayFragmentModel

    private var playBtnChecked: Boolean = false;
    private lateinit var nameScrollTimer: CountDownTimer
    val itemData: ObservableField<Audio> = ObservableField<Audio>()
    val duration: ObservableField<String> = ObservableField<String>("")
    val progressMax: ObservableInt = ObservableInt(0)
    val progress: ObservableInt = ObservableInt(0)
    val mediaPlayer: MediaPlayer by lazy { audioPlayFragmentModel.mediaPlayer }
    private var progressSubscription: Subscription? = null
    private var durationSubscription: Subscription? = null
    private var playItemDataSubscription: Subscription? = null

    fun onCreateView(ctx: AudioPlayFullscreenFragment) {
        this.ctx = ctx
        val mainActivityComponent = (ctx.activity?.application as App).mainActivityComponent
        mainActivityComponent?.inject(this)
        ctx.binding.model = this
    }

    fun onStart() {
        initUiData()
        initNameScrollTimer()
        setOrientationBaseLayout()
        imageScrollInit()
        seekbarInit()
        playBtnsInit()
        subscribeOnDataChange()
    }

    fun onDestroy() {
        if (progressSubscription != null) {
            progressSubscription?.unsubscribe()
        }
        if (durationSubscription != null) {
            durationSubscription?.unsubscribe()
        }
        if (playItemDataSubscription != null) {
            playItemDataSubscription?.unsubscribe()
        }
    }

    private fun initUiData() {
        itemData.set(model.playItemData)
        duration.set(audioPlayFragmentModel.duration.get())
    }

    private fun subscribeOnDataChange() {
        progressSubscription = audioPlayFragmentModel.progressObservable
            .subscribeOn(Schedulers.newThread())
            .subscribe {
            progress.set(it)
        }
        durationSubscription = audioPlayFragmentModel.durationObservable
            .subscribeOn(Schedulers.newThread())
            .subscribe {
            duration.set(it)
        }
        playItemDataSubscription = model.playItemDataObservable
            .subscribeOn(Schedulers.newThread())
            .subscribe {
                ctx.binding.audioImageScrollList.currentItem = model.playItemPosition
                itemData.set(it)
                initUiData()
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
                    // обновляем данные
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
            audioPlayFragmentModel.audioTimer?.start()
        } else {
            mediaPlayer.pause()
            ctx.binding.playPauseBtn.setImageResource(R.drawable.play_fullscr)
            audioPlayFragmentModel.audioTimer?.cancel()
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