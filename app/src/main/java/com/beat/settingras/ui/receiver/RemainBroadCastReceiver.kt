package com.beat.settingras.ui.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.beat.settingras.AppLog
import com.beat.settingras.Constant

/**
 * 잔존 음료에 대한 AlarmReceiver
 * AlarmManager의 PendingIntent의 cancel 시 별도로 표기하기 위함
 */
class RemainBroadCastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(context!=null && intent != null){
            AppLog.l()
            var serviceIntent = Intent(Constant.INTENT_ACION.ACTION_ALARM_HEALTH_RECEIVE).apply {
                if(intent.hasExtra(Constant.KEY.TYPE))
                    putExtra(Constant.KEY.TYPE,intent.getIntExtra(Constant.KEY.TYPE,-1))
            }
            LocalBroadcastManager.getInstance(context).sendBroadcast(serviceIntent)
        }
    }
}