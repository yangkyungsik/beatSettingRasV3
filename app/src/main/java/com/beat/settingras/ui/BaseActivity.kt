package com.beat.settingras.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.beat.settingras.AppLog
import com.beat.settingras.R
import com.beat.settingras.ui.widget.CommonProgressDialog
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.reflect.KClass

abstract class BaseActivity<VM : BaseViewModel>(
    clazz: KClass<VM>
) : AppCompatActivity() {

    protected val viewModel: VM by viewModel(clazz) //Reflection class를 이용한 ViewModel Read
    private var mProgressDialog: CommonProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        setBaseObserver()
    }

    fun setBaseObserver() {
        viewModel.refreshing.observe(this, Observer {
            if (it == true) {
                AppLog.d(TAG, "showProgressBar")
                showProgressDialogView()
            } else {
                AppLog.d(TAG, "hideProgressBar")
                hideProgressDialogView()
            }
        })
        //TODO 테스트용
        viewModel.finish.observe(this, Observer {
            if (it == true) {
                finish()
            }
        })
        viewModel.toast.observe(this, Observer {
            if (!it.isNullOrEmpty())
                Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
        })

        viewModel.newIntent.observe(this, Observer {
            it?.let {
                startActivity(it)
            }
        })

        viewModel.finishIntent.observe(this, Observer {
            it?.let {
                startActivity(it)
                finish()
            }
        })

    }

    fun getProgressDialog(): CommonProgressDialog? {
        if (mProgressDialog == null) {
            mProgressDialog = CommonProgressDialog(BaseActivity@ this)
        }
        return mProgressDialog
    }

    fun showProgressDialogView() {
        showProgressDialogView(true, null)
    }

    private fun showProgressDialogView(dimFlag: Boolean, progressMsg: String?) {
        try {
            var window = getProgressDialog()?.window

            window?.let {
                if (dimFlag) {
                    var params = it.attributes
                    params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
                    params.dimAmount = 0.75f
                    it.attributes = params
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                }
            }

            getProgressDialog()?.let {
                it.show()

                val tvProgress = it.findViewById(R.id.tv_progress) as TextView

                if (!TextUtils.isEmpty(progressMsg)) {
                    tvProgress.visibility = View.VISIBLE
                    tvProgress.text = progressMsg
                } else {
                    tvProgress.visibility = View.GONE
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun hideProgressDialogView() {
        try {
            mProgressDialog?.let { it.dismiss() }
            mProgressDialog = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    fun startSetting(act: Activity, value: ActivityInfo) {
        try {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            intent.setClassName(
                value.packageName,
                value.name
            )
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            act.startActivity(intent)
        } catch (ignore: Exception) {

        }

    }

    fun findSettingPackages(context: Context): ActivityInfo {
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val intentList = context.packageManager.queryIntentActivities(intent, 0)

        for (info in intentList) {
            val activityInfo = info.activityInfo
            if (activityInfo.applicationInfo.packageName == "com.android.settings") {
                return activityInfo
            }
        }
        val ai = ActivityInfo()
        ai.packageName = "null"
        return ai
    }

    companion object {
        val TAG: String = BaseActivity::class.java.simpleName
    }

}