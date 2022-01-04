package com.like

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.like.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val model: MainActivityModel by lazy { ViewModelProvider(this)[MainActivityModel::class.java] }
    val binding: ActivityMainBinding by lazy { DataBindingUtil.setContentView(this, R.layout.activity_main) }
    var savedInstanceState: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.savedInstanceState = savedInstanceState
        model.onCreate(this)
    }

    override fun onResume() {
        super.onResume()
        model.onResume(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        // очищаем dagger компонент MainActivity чтобы он не остался в памяти
        val appComponent = (application as App)
        appComponent.mainActivityComponent = null
    }
}