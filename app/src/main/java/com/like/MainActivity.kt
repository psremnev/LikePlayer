package com.like

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.like.databinding.ActivityMainBinding
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject lateinit var model: MainActivityModel
    val binding: ActivityMainBinding by lazy { DataBindingUtil.setContentView(this, R.layout.activity_main) }
    var savedInstanceState: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // ставим обычную ориентацию и тему после splash экрана
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        setTheme(R.style.Theme_Like)
        super.onCreate(savedInstanceState)

        // создаем компоннет даггер MainActivity
        val app = (application as App)
        app.createMainActivityComponent(this)
        app.mainActivityComponent?.inject(this)

        this.savedInstanceState = savedInstanceState
        model.onCreate(this)
    }

    override fun onResume() {
        super.onResume()
        model.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        model.onDestroy()
        // очищаем dagger компонент MainActivity чтобы он не остался в памяти
        val appComponent = (application as App)
        appComponent.clearMainActivityComponent()
    }
}