package com.beat.settingras.ui.view

import android.os.Bundle
import com.beat.settingras.Constant
import com.beat.settingras.databinding.ActivityReadTextBinding
import com.beat.settingras.ui.BaseActivity
import com.beat.settingras.ui.BaseViewModel
import com.beat.settingras.util.CommonUtil

class ReadTextActivity : BaseActivity<BaseViewModel>(BaseViewModel::class) {

    lateinit var binding:ActivityReadTextBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadTextBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var text:String? = CommonUtil.getIntentExtra(intent,Constant.KEY.DATA,"")
        binding.tvScroll.text = text
    }

    override fun initListener() {

    }

    override fun initObserver() {

    }
}