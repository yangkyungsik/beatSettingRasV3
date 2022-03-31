package com.beat.settingras.ui.view

import android.os.Bundle
import androidx.lifecycle.Observer
import com.beat.settingras.databinding.ActivitySslLoginBinding
import com.beat.settingras.ui.BaseActivity
import com.beat.settingras.ui.model.SslLoginViewModel

/**
 * SSL 연결 화면
 * 데이터 전달
 */
class SslLoginActivity : BaseActivity<SslLoginViewModel>(SslLoginViewModel::class) {

    private lateinit var binding: ActivitySslLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        initObserver()
    }

    private fun init() {
        binding = ActivitySslLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnLogin.setOnClickListener {
            viewModel.validateData(
                binding.etIp.text.toString(), binding.etPort.text.toString(), binding
                    .etId.text.toString(), binding.etPw.text.toString()
            )
        }
    }

    private fun initObserver() {
        viewModel.playerState.observe(this, Observer {
            if (it == true) {

            }
        })
    }
}