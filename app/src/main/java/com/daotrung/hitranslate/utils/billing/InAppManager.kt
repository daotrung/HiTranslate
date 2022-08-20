package com.daotrung.hitranslate.utils.billing

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import com.daotrung.hitranslate.base.Params
import com.daotrung.hitranslate.utils.interfaces.PurchaseCallback
import java.util.*
import kotlin.collections.ArrayList


class InAppManager private constructor() {
    private var billingClient: BillingClient? = null
    private var callback: PurchaseCallback? = null
    private var skuDetailsListIAP: List<SkuDetails>? = ArrayList()
    private var skuDetailsListSUB: List<SkuDetails>? = ArrayList()
    private var isPurchased = false
    private val acknowledgePurchaseResponseListener =
        AcknowledgePurchaseResponseListener { callback!!.purchaseSuccess() }

    fun setPurchased(purchased: Boolean) {
        isPurchased = purchased
    }

    fun setCallback(callback: PurchaseCallback?) {
        this.callback = callback
    }

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases -> // To be implemented in a later section.
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK
                && purchases != null
            ) {
                for (i in purchases.indices) {
                    handlePurchase(purchases[i])
                }
            } else {
                callback!!.purchaseFail()
            }
        }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient!!.acknowledgePurchase(
                    acknowledgePurchaseParams,
                    acknowledgePurchaseResponseListener
                )
            }
        }
    }

    private fun initBilling(context: Context?) {
        billingClient = context?.let {
            BillingClient.newBuilder(it)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build()
        }
        billingClient?.let {
            it.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        // The BillingClient is ready. You can query purchases here.
                        val params = SkuDetailsParams.newBuilder()
                        params.setSkusList(Collections.singletonList(Params.PRODUCT_PURCHASE))
                            .setType(BillingClient.SkuType.INAPP)
                        it.querySkuDetailsAsync(params.build(),
                            SkuDetailsResponseListener { _: BillingResult?, skuDetailsList: List<SkuDetails>? ->
                                skuDetailsListIAP = skuDetailsList
                            })
                        params.setSkusList(listOf(PRODUCT_SUB_YEAR, PRODUCT_SUB_MONTH))
                            .setType(BillingClient.SkuType.SUBS)
                        it.querySkuDetailsAsync(params.build(),
                            SkuDetailsResponseListener { _: BillingResult?, skuDetailsList: List<SkuDetails>? ->
                                skuDetailsListSUB = skuDetailsList
                            })
                        val isVip = isRemovedAds(context)
                        Log.e(TAG, "isRemovedAds= $isVip")
                    }
                }

                override fun onBillingServiceDisconnected() {
                }
            })
        }
    }

    fun consume(productId: String) {
        val purchase = getPurchase(productId)
        if (purchase != null) {
            val consumeParams =
                ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
            billingClient!!.consumeAsync(
                consumeParams
            ) { _: BillingResult?, _: String? -> }
        }
    }

    private fun getPurchase(productId: String): Purchase? {
        val purchasesResult = billingClient!!.queryPurchases(BillingClient.SkuType.INAPP)
        for (purchase in Objects.requireNonNull(purchasesResult.purchasesList)) {
            if (purchase.sku == productId) return purchase
        }
        return null
    }

    fun isRemovedAds(context: Context?): Boolean {
        if (billingClient == null) {
            if (context == null) {
                return false
            }
            initBilling(context)
        }
        return isPurchased() || isSubscribed
        //        return true;
    }

    fun hasSubVip(): Boolean {
        return isPurchased
    }

    private fun isPurchased(): Boolean {
        val purchasesResult = billingClient!!.queryPurchases(BillingClient.SkuType.INAPP)
        if (purchasesResult == null || purchasesResult.purchasesList == null) return false
        Log.e(TAG, "isPurchased " + purchasesResult.purchasesList!!.size)
        for (purchase in purchasesResult.purchasesList!!) {
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                return true
            }
        }
        return false
    }

    private val isSubscribed: Boolean
        private get() {
            val purchasesResult = billingClient!!.queryPurchases(BillingClient.SkuType.SUBS)
            if (purchasesResult == null || purchasesResult.purchasesList == null) return false
            for (purchase in purchasesResult.purchasesList!!) {
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    return true
                }
            }
            return false
        }

    fun purchase(activity: Activity?, productId: String) {
        if (billingClient == null) {
            activity?.let { initBilling(it) }
        }
        val skuDetails = getSkuDetail(skuDetailsListIAP, productId) ?: return
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails)
            .build()
        billingClient!!.launchBillingFlow(activity!!, billingFlowParams)
    }

    fun subscribe(activity: Activity?, productId: String) {
        if (billingClient == null) {
            activity?.let { initBilling(it) }
        }
        try {
            val skuDetails = getSkuDetail(skuDetailsListSUB, productId) ?: return
            val billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build()
            billingClient!!.launchBillingFlow(activity!!, billingFlowParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getSkuDetail(skuDetailsListSUB: List<SkuDetails>?, productId: String): SkuDetails? {
        for (skuDetails in skuDetailsListSUB!!) {
            if (skuDetails.sku == productId) {
                return skuDetails
            }
        }
        return null
    }

    fun getPrice(productId: String): String {
        if (billingClient == null || !billingClient!!.isReady) {
            return ""
        }
        for (skuDetails in skuDetailsListSUB!!) {
            if (skuDetails.sku == productId) {
                return skuDetails.price
            }
        }
        for (skuDetails in skuDetailsListIAP!!) {
            if (skuDetails.sku == productId) {
                return skuDetails.price
            }
        }
        return ""
    }

    companion object {
        private val TAG = InAppManager::class.java.name
        const val PRODUCT_SUB_YEAR = "mirroring_pro_year"
        const val PRODUCT_SUB_MONTH = "mirroring_pro_month"

        @get:Synchronized
        @SuppressLint("StaticFieldLeak")
        var instance: InAppManager? = null
            get() {
                if (field == null) {
                    field = InAppManager()
                }
                return field
            }
            private set
    }
}
