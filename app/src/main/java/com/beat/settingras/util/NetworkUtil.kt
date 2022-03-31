package com.beat.settingras.util

import android.annotation.TargetApi
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import com.beat.settingras.AppLog
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface

object NetworkUtil {
    fun isNetworkConnected(context:Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        return isConnected
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun isEtherNet(context:Context):Boolean{
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        cm?.let {
            cm?.activeNetworkInfo?.let {
                return it.type == ConnectivityManager.TYPE_ETHERNET
            }
            return false
        }
        return false
    }

    fun getMyIP():String{
        var mIP = ""
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    val address = InetAddress.getByName(inetAddress.hostAddress)
                    if (!inetAddress.isLoopbackAddress && address is Inet4Address) {
                        mIP = inetAddress.hostAddress
                        AppLog.d("ip : $mIP")
                        return mIP
                    }
                }
            }
            mIP
        }
        catch (e:Exception){
            e.printStackTrace()
            return mIP
        }
        return mIP
    }

}