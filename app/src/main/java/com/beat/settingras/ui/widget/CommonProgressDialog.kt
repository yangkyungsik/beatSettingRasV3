package com.beat.settingras.ui.widget

import android.content.Context
import android.os.Bundle
import com.beat.settingras.R
import com.beat.settingras.util.CommonUtil


/**
 * 공통 ProgressDialog
 */
class CommonProgressDialog(private var mContext: Context, theme: Int) : BaseDialog(mContext, theme) {

    constructor(context: Context) : this(context, R.style.AppTheme_Translucent) {
        mContext = context
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_progress)

        val windows = window

        windows?.let {
            it.setWindowAnimations(R.style.AppTheme_Translucent)
        }

        if (CommonUtil.isLolliPopOrLater)
            CommonUtil.setStatusNavigationBarBG(mContext, windows, R.color.common_transparent, R.color.c_000000)
        else
            CommonUtil.setStatusNavigationBarBG(mContext, windows, R.color.c_000000, R.color.c_000000)
        setCancelable(true) //백 버튼으로 취소 가능.
        setCanceledOnTouchOutside(true) //터치로 취소는 불가능.
    }

    private fun setupViews() {}

    companion object {
        val TAG = CommonProgressDialog::class.java.simpleName
    }

}
