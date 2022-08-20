package com.daotrung.hitranslate.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.daotrung.hitranslate.App
import com.daotrung.hitranslate.base.Params.FONT_SIZE
import com.daotrung.hitranslate.base.Params.KEY_OPEN_APP
import com.daotrung.hitranslate.utils.billing.InAppManager

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        val application = application as? App

        if (application == null) {
            startActivity()
            return
        }

        if (!InAppManager.instance!!.isRemovedAds(this)) {
            application.showAdIfAvailable(
                this@SplashScreenActivity,
                object : App.OnShowAdCompleteListener {
                    override fun onShowAdComplete() {
                        startActivity()
                    }
                })
        } else {
            startActivity()
        }
        if (App.dataPer.getInt(FONT_SIZE, 0) == 0) {
            App.dataPer.putInt(FONT_SIZE, 14)
        }
    }

    private fun startActivity() {
        if (App.dataPer.getBoolean(KEY_OPEN_APP, true)) {
            startActivity(Intent(this@SplashScreenActivity, PreviewActivity::class.java))
            finish()
        } else {
            startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
            finish()
        }
    }
}