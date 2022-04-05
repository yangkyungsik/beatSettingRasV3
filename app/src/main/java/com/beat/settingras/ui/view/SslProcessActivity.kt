package com.beat.settingras.ui.view

import android.os.Bundle
import androidx.lifecycle.Observer
import com.beat.settingras.Constant
import com.beat.settingras.R
import com.beat.settingras.databinding.ActivitySslProcessBinding
import com.beat.settingras.ui.BaseActivity
import com.beat.settingras.ui.model.SslProcessViewModel
import com.beat.settingras.util.CommonUtil

class SslProcessActivity : BaseActivity<SslProcessViewModel>(SslProcessViewModel::class) {

    private lateinit var binding: ActivitySslProcessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySslProcessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.init(CommonUtil.getIntentExtra(intent,Constant.KEY.IP,""),
            CommonUtil.getIntentExtra(intent,Constant.KEY.PORT,0),
            CommonUtil.getIntentExtra(intent,Constant.KEY.USERNAME,""),
            CommonUtil.getIntentExtra(intent,Constant.KEY.PW,""),
            CommonUtil.getIntentExtra(intent,Constant.KEY.STORECODE,""))

        binding.btnSendMsg1.setOnClickListener {
            val arr = resources.getStringArray(R.array.init_bash_profile)
            viewModel.sendMsgArray(arr)
        }

        binding.btnSendMsg2.setOnClickListener {

        }

        viewModel.cmdText.observe(this, Observer {
            binding.tvScroll.append(it)
            binding.scrollview.scrollTo(0, binding.tvScroll.bottom)
        })
    }
}