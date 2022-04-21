package com.beat.settingras.ui.view

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.beat.settingras.AppLog
import com.beat.settingras.Constant
import com.beat.settingras.databinding.ActivitySslLoginBinding
import com.beat.settingras.ui.BaseActivity
import com.beat.settingras.ui.model.SslLoginViewModel
import com.beat.settingras.util.CommonUtil
import com.google.gson.JsonObject
import org.json.JSONObject

/**
 * SSL 연결 화면
 * 데이터 전달
 */
class SslLoginActivity : BaseActivity<SslLoginViewModel>(SslLoginViewModel::class) {

    private lateinit var binding: ActivitySslLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySslLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val key = "user.home"
        val path: String = applicationInfo.dataDir
        System.setProperty(key, path)

        showProgressDialogView()
        binding.etId.setText("beat")
        binding.etPort.setText("2222")
        binding.etPw.setText("qlxmXbeat!!")
        binding.etStoreCode.setText("X6011")
    }


    override fun initObserver() {
        viewModel.validate.observe(this, Observer {
            if (it == true) {
                var intent = Intent(applicationContext, SslProcessActivity::class.java).apply {
                    putExtra(Constant.KEY.IP,binding.etIp.text.toString())
                    putExtra(Constant.KEY.PORT,binding.etPort.text.toString().toInt())
                    putExtra(Constant.KEY.USERNAME,binding.etId.text.toString())
                    putExtra(Constant.KEY.PW,binding.etPw.text.toString())
                    putExtra(Constant.KEY.STORECODE,binding.etStoreCode.text.toString())
                }
                startActivity(intent)
            }
        })
    }

    override fun initListener() {
        binding.btnLogin.setOnClickListener {
            viewModel.validateData(
                binding.etIp.text.toString(), binding.etPort.text.toString(), binding
                    .etId.text.toString(), binding.etPw.text.toString(), binding.etStoreCode.text.toString()
            )
        }
    }
}