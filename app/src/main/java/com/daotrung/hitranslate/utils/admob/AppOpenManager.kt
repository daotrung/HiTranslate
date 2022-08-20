package com.daotrung.hitranslate.utils.admob

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.daotrung.hitranslate.App
import com.daotrung.hitranslate.App.Companion.isShowingAd
import com.daotrung.hitranslate.BuildConfig
import com.daotrung.hitranslate.utils.LogInstance
import com.daotrung.hitranslate.utils.NetworkUtils
import com.daotrung.hitranslate.utils.billing.InAppManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import java.util.*

open class AppOpenManager {

    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    private var loadTime: Long = 0

    fun loadAd(context: Context) {
        if(!InAppManager.instance!!.isRemovedAds(context)){
            if (isLoadingAd || isAdAvailable()) {
                return
            }
            isLoadingAd = true
            val request = AdRequest.Builder().build()
            AppOpenAd.load(
                context,
                BuildConfig.adsOpenId,
                request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                object : AppOpenAd.AppOpenAdLoadCallback() {

                    override fun onAdLoaded(ad: AppOpenAd) {
                        appOpenAd = ad
                        isLoadingAd = false
                        loadTime = Date().time
                        LogInstance.e("onAdLoaded.")
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        isLoadingAd = false
                        LogInstance.e(loadAdError.message)
                    }
                })
        }
    }

    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference: Long = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
    }

    fun showAdIfAvailable(activity: Activity) {
        showAdIfAvailable(
            activity,
            object : App.OnShowAdCompleteListener {
                override fun onShowAdComplete() {
                }
            })
    }


    fun showAdIfAvailable(
        activity: Activity,
        onShowAdCompleteListener: App.OnShowAdCompleteListener
    ) {
        if (isShowingAd) {
            LogInstance.e("The app open ad is already showing.")
            return
        }

        if (!isAdAvailable()) {
            LogInstance.e("The app open ad is not ready yet.")
            onShowAdCompleteListener.onShowAdComplete()
            loadAd(activity)
            return
        }

        LogInstance.e("Will show ad.")

        appOpenAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                appOpenAd = null
                isShowingAd = false
                LogInstance.e("onAdDismissedFullScreenContent.")
                onShowAdCompleteListener.onShowAdComplete()
                loadAd(activity)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                appOpenAd = null
                isShowingAd = false
                LogInstance.e(adError.message)
                onShowAdCompleteListener.onShowAdComplete()
                loadAd(activity)
            }

            override fun onAdShowedFullScreenContent() {
                if(App.numberOpen==5){
                    App.numberOpen -= 1
                }
                LogInstance.e("onAdShowedFullScreenContent")
            }
        }
        isShowingAd = true
        appOpenAd!!.show(activity)
    }
}
//class AppOpenManager(private val myApp: App) : LifecycleObserver,
//    Application.ActivityLifecycleCallbacks {
//
//    init {
//        myApp.registerActivityLifecycleCallbacks(this)
//        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
//    }
//
//    private var appOpenAd: AppOpenAd? = null
//    private var loadCallBack: AppOpenAd.AppOpenAdLoadCallback? = null
//
//    private var loadTime: Long = 0
//
//    private fun fetchAds() {
//        if (isAdAvailable() && !NetworkUtils.instance.isOnline()) {
//            return
//        }
//        loadCallBack = object : AppOpenAd.AppOpenAdLoadCallback() {
//            override fun onAdLoaded(p0: AppOpenAd) {
//                appOpenAd = p0
//                loadTime = Date().time
//                LogInstance.e("onAdLoaded")
//            }
//
//            override fun onAdFailedToLoad(p0: LoadAdError) {
//                appOpenAd = null
//                LogInstance.e("onAdFailedToLoad")
//            }
//        }
//        val adRequest = getAdRequest()
//        AppOpenAd.load(
//            myApp, BuildConfig.adsOpenId,
//            adRequest, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallBack
//        )
//    }
//
//    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
//        val dateDifference = Date().time - loadTime
//        val numMilliSecondsPerHour: Long = 3600000
//        return dateDifference < numMilliSecondsPerHour * numHours
//    }
//
//    private fun getAdRequest(): AdRequest {
//        return AdRequest.Builder().build()
//    }
//
//    fun isAdAvailable(): Boolean {
//        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
//    }
//
//    private var isShowingAd = false
//    private fun showAdIfAvailable() {
//        if (!isShowingAd && isAdAvailable() && NetworkUtils.instance.isOnline()) {
//            val fullScreenContentCallback: FullScreenContentCallback =
//                object : FullScreenContentCallback() {
//                    override fun onAdDismissedFullScreenContent() {
//                        appOpenAd = null
//                        isShowingAd = false
//                        fetchAds()
//                    }
//
//                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
//                    }
//
//                    override fun onAdShowedFullScreenContent() {
//                        isShowingAd = true
//                    }
//                }
//            appOpenAd?.fullScreenContentCallback = fullScreenContentCallback
//            appOpenAd?.show(currentActivity)
//        } else {
//            LogInstance.e("can not show ad")
//            fetchAds()
//        }
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_START)
//    fun onMoveToForeground() {
//        if (!App.showing) {
//            showAdIfAvailable()
//        }
//        LogInstance.e("onStart")
//    }
//
//    private var currentActivity: Activity? = null
//    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
//    }
//
//    override fun onActivityStarted(p0: Activity) {
//        currentActivity = p0
//    }
//
//    override fun onActivityResumed(p0: Activity) {
//        currentActivity = p0
//    }
//
//    override fun onActivityPaused(p0: Activity) {
//    }
//
//    override fun onActivityStopped(p0: Activity) {
//    }
//
//    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
//    }
//
//    override fun onActivityDestroyed(p0: Activity) {
//        currentActivity = null
//    }
//
//}