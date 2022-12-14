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
            JSONObject(CommonUtil.readAsset(applicationContext, BuildConfig.SSH_CMD_FILENAME, ""))
        )
    }

    override fun initObserver() {
        viewModel.cmdText.observe(this) {
            binding.tvScroll.append(it)
            binding.scrollview.postDelayed(Runnable {
                binding.scrollview.smoothScrollTo(0, binding.tvScroll.bottom)
            }, 500)

        }
        viewModel.readText.observe(this) {
            if (!it.isNullOrEmpty()) {
                var intent = Intent(applicationContext, ReadTextActivity::class.java).apply {
                    putExtra(Constant.KEY.DATA, it)
                }
                startActivity(intent)
            }
        }
    }

    override fun initListener() {
        binding.btnDownload.setOnClickListener {
            viewModel.downloadAllModule(getString(R.string.cmd_download_module))
        }
        binding.btnWriteBash.setOnClickListener {
            viewModel.sendMsgArray(getString(R.string.cmd_init_bash_profile))
        }

        binding.btnRemoveBash.setOnClickListener {
            viewModel.sendMsgArray(getString(R.string.cmd_remove_bash_profile))
        }

        binding.btnReadBash.setOnClickListener {
            viewModel.readFile(getString(R.string.cmd_read_bash_profile))
        }

        binding.btnSourceBash.setOnClickListener {
            viewModel.sendMsgArray(getString(R.string.cmd_source_bash_profile))
        }

        binding.btnCheckExportValue.setOnClickListener {
            viewModel.readFile(getString(R.string.cmd_check_export_value))
        }

        binding.btnRebootRas.setOnClickListener {
            viewModel.sendMsgArray(
                getString(R.string.cmd_reboot_raspberry),
                isAuth = true,
                isFinish = true
            )
        }
    }

}