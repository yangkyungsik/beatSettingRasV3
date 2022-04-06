package com.beat.settingras.ui.view

import android.content.Intent
import android.os.Bundle
import com.beat.settingras.BuildConfig
import com.beat.settingras.Constant
import com.beat.settingras.R
import com.beat.settingras.databinding.ActivitySslProcessBinding
import com.beat.settingras.ui.BaseActivity
import com.beat.settingras.ui.model.SslProcessViewModel
import com.beat.settingras.util.CommonUtil
import org.json.JSONObject

class SslProcessActivity : BaseActivity<SslProcessViewModel>(SslProcessViewModel::class) {

    private lateinit var binding: ActivitySslProcessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySslProcessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.init(
            CommonUtil.getIntentExtra(intent, Constant.KEY.IP, ""),
            CommonUtil.getIntentExtra(intent, Constant.KEY.PORT, 0),
            CommonUtil.getIntentExtra(intent, Constant.KEY.USERNAME, ""),
            CommonUtil.getIntentExtra(intent, Constant.KEY.PW, ""),
            CommonUtil.getIntentExtra(intent, Constant.KEY.STORECODE, ""),
            JSONObject(CommonUtil.readAsset(applicationContext, BuildConfig.SSH_CMD_FILENAME,""))
        )
    }

    override fun initObserver() {
        viewModel.cmdText.observe(this) {
            binding.tvScroll.append(it)
            binding.scrollview.scrollTo(0, binding.tvScroll.bottom)
        }
        viewModel.readText.observe(this) {
            if(!it.isNullOrEmpty()) {
                var intent = Intent(applicationContext, ReadTextActivity::class.java).apply {
                    putExtra(Constant.KEY.DATA, it)
                }
                startActivity(intent)
            }
        }
    }

    override fun initListener() {
        binding.btnSendMsg1.setOnClickListener {
            viewModel.sendMsgArray(getString(R.string.cmd_init_bash_profile))
        }

        binding.btnSendMsg2.setOnClickListener {
            viewModel.sendMsgArray(getString(R.string.cmd_remove_bash_profile))
        }

        binding.btnSendMsg3.setOnClickListener {
            viewModel.readFile(getString(R.string.cmd_read_bash_profile))
        }

        binding.btnSendMsg4.setOnClickListener {
            viewModel.sendMsgArray(getString(R.string.cmd_source_bash_profile))
        }
        binding.btnSendMsg5.setOnClickListener {
            viewModel.sendMsgArray(getString(R.string.cmd_reboot_raspberry),true)
        }
    }


}