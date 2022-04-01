package com.beat.settingras.ui.view

import android.os.Bundle
import com.beat.settingras.Constant
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
            CommonUtil.getIntentExtra(intent,Constant.KEY.PW,""))
    }
}