package com.daotrung.hitranslate.utils.admob

import android.app.Activity
import com.daotrung.hitranslate.App
import com.daotrung.hitranslate.BuildConfig
import com.daotrung.hitranslate.utils.LogInstance
import com.daotrung.hitranslate.utils.NetworkUtils
import com.daotrung.hitranslate.utils.billing.InAppManager
import com.daotrung.hitranslate.utils.interfaces.AdmobListener
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object InterstitialManager {
    fun loadInterstitial(activity: Activity) {
        if (!InAppManager.instance!!.isRemovedAds(activity)){
            if (App.interstitialAd == null && !App.showingInterstitialAd && NetworkUtils.instance.isOnline()) {
                val adRequest = AdRequest.Builder().build()
                val idAds = BuildConfig.adsFullId
                InterstitialAd.load(
                    activity, idAds, adRequest, object : InterstitialAdLoadCallback() {
                        override fun onAdFailedToLoad(p0: LoadAdError) {
                            App.interstitialAd = null
                            App.showingInterstitialAd = false
                            LogInstance.e("onAdFailedToLoad")
                            super.onAdFailedToLoad(p0)
                        }

                        override fun onAdLoaded(p0: InterstitialAd) {
                            App.interstitialAd = p0
                            App.showingInterstitialAd = false
                            LogInstance.e("onAdLoaded")
                            super.onAdLoaded(p0)
                        }
                    }
                )
            }
        }
    }

    fun showAds(activity: Activity) {
        if (App.isShowingAd) {
            return
        }
        LogInstance.e(App.numberOpen)
        if (App.numberOpen < 5) {
            App.numberOpen+=1
            if (App.numberOpen == 4) {
                loadInterstitial(activity)
            }
            return
        } else {
            if (!InAppManager.instance!!.isRemovedAds(activity)){
                if (App.interstitialAd != null && NetworkUtils.instance.isOnline()) {
                    App.interstitialAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                App.interstitialAd = null
                                App.showingInterstitialAd = false
                                App.numberOpen = 0
                                super.onAdDismissedFullScreenContent()
                            }

                            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                                App.interstitialAd = null
                                App.showingInterstitialAd = false
                                super.onAdFailedToShowFullScreenContent(p0)
                            }

                            override fun onAdShowedFullScreenContent() {
                                App.interstitialAd = null
                                App.showingInterstitialAd = true
                                super.onAdShowedFullScreenContent()
                            }
                        }
                    App.interstitialAd?.show(activity)
                }
            }
        }
    }
}