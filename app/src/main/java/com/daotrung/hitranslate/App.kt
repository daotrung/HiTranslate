package com.daotrung.hitranslate

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.room.Room
import com.daotrung.hitranslate.base.Params
import com.daotrung.hitranslate.database.AppDatabase
import com.daotrung.hitranslate.utils.NetworkUtils
import com.daotrung.hitranslate.utils.admob.AppOpenManager
import com.daotrung.hitranslate.utils.billing.InAppManager
import com.daotrung.hitranslate.viewmodel.HistoryViewModel
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.tencent.mmkv.MMKV

class App : Application(), Application.ActivityLifecycleCallbacks, LifecycleObserver {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        lateinit var dataPer: MMKV
        private lateinit var db: AppDatabase
        fun getDb() = db
        var fontSize: Int = 14
        lateinit var historyViewModel: HistoryViewModel

        var numberOpen = 5
        var interstitialAd: InterstitialAd? = null
        var showingInterstitialAd: Boolean = false
        var isShowingAd : Boolean = false

    }

    private var currentActivity: Activity? = null
    private lateinit var appOpenAdManager: AppOpenManager

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        MMKV.initialize(this)
        dataPer = MMKV.defaultMMKV()
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "data")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
        if (dataPer.getInt(Params.FONT_SIZE, 0) != 0) {
            fontSize = dataPer.getInt(Params.FONT_SIZE, 0)
        }
        MobileAds.initialize(this) {}
        historyViewModel = HistoryViewModel()
        NetworkUtils.instance.initializeWithApplicationContext(this)
        appOpenAdManager = AppOpenManager()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() {
        currentActivity?.let {
            if (!InAppManager.instance!!.isRemovedAds(it)){
                appOpenAdManager.showAdIfAvailable(it)
            }
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {
        if (!isShowingAd) {
            currentActivity = activity
        }
    }

    override fun onActivityResumed(activity: Activity) {}

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {}

    fun showAdIfAvailable(
        activity: Activity,
        onShowAdCompleteListener: OnShowAdCompleteListener
    ) {
        appOpenAdManager.showAdIfAvailable(activity, onShowAdCompleteListener)
    }

    interface OnShowAdCompleteListener {
        fun onShowAdComplete()
    }
}