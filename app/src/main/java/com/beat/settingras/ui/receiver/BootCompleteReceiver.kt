package com.beat.settingras.ui.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_BOOT_COMPLETED
import com.beat.settingras.AppLog
import com.beat.settingras.ui.view.CommonWebViewActivity

class BootCompleteReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        AppLog.d("TAG","onReceived BootCompleteReceiver")
        if(intent?.action == ACTION_BOOT_COMPLETED) {

            context?.let {
                var bootIntent = Intent()
                bootIntent.setClass(it, CommonWebViewActivity::class.java)
                bootIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(bootIntent)
            }
        }
    }
}