package com.beat.settingras.ui.view

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import com.beat.settingras.R
import com.beat.settingras.databinding.ActivityMainBinding
import com.beat.settingras.ui.BaseActivity
import com.beat.settingras.ui.model.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<MainViewModel>(MainViewModel::class){

    private val mainViewModel : MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initListener() {
    }

    override fun initObserver() {
        mainViewModel.test.observe(this, Observer {
            Toast.makeText(applicationContext,it.data.androidNotice,Toast.LENGTH_SHORT).show()
        })

    }
}