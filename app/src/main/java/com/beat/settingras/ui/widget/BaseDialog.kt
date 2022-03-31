package com.beat.settingras.ui.widget


import android.app.Dialog
import android.content.Context

/**
 * 모든 다이얼로그의 부모 클래스
 */
open class BaseDialog : Dialog {

    companion object {
        val TAG = "BaseDialog"
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, theme: Int) : super(context, theme)

    override fun show() {

        try {
            super.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun dismiss() {
        try {
            super.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
