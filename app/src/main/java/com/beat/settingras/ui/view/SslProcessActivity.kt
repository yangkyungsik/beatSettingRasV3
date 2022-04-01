package com.beat.settingras.ui.view

import android.os.Bundle
import com.beat.settingras.databinding.ActivitySslProcessBinding
import com.beat.settingras.ui.BaseActivity
import com.beat.settingras.ui.model.SslProcessViewModel

class SslProcessActivity : BaseActivity<SslProcessViewModel>(SslProcessViewModel::class) {

    private lateinit var binding: ActivitySslProcessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySslProcessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.init("12",1,"1","11")
    }
}