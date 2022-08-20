package com.daotrung.hitranslate.activity

import android.app.Dialog
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.daotrung.hitranslate.App
import com.daotrung.hitranslate.R
import com.daotrung.hitranslate.base.Params
import com.daotrung.hitranslate.base.Params.FONT_SIZE
import com.daotrung.hitranslate.databinding.ActivityMenuBinding
import com.daotrung.hitranslate.databinding.DialogFontSizeBinding
import com.daotrung.hitranslate.databinding.DialogTextBinding
import com.daotrung.hitranslate.utils.AppUtils.sendSupportGmail
import com.daotrung.hitranslate.utils.AppUtils.shareApplicationId
import com.daotrung.hitranslate.utils.AppUtils.writeReview
import com.daotrung.hitranslate.utils.LogInstance
import com.daotrung.hitranslate.utils.billing.InAppManager
import com.daotrung.hitranslate.utils.interfaces.PurchaseCallback
import com.daotrung.hitranslate.utils.widget.CustomToast

class MenuActivity : AppCompatActivity(), PurchaseCallback {
    private lateinit var binding: ActivityMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        window.statusBarColor = this.resources.getColor(R.color.colorPrimaryDark)
        setContentView(binding.root)
        binding.switchBar.isChecked = App.dataPer.getBoolean(Params.ON_OFF_NOTIFICATION, true)
        toolbar()
        onClick()
    }

    private fun toolbar() {
        binding.toolbarMenuBack.title = "Menu"
        setSupportActionBar(binding.toolbarMenuBack)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbarMenuBack.setNavigationOnClickListener { onBackPressed() }
    }

    private fun onClick() {
        binding.linearFont.setOnClickListener {
            showFontSize()
        }
        binding.linearShare.setOnClickListener {
            shareApplicationId()
        }
        binding.linearRate.setOnClickListener {
            writeReview()
        }
        binding.linearSupport.setOnClickListener {
            sendSupportGmail()
        }
        binding.linearPrivacy.setOnClickListener {
            showText("Privacy Policy", getString(R.string.privacy_content))
        }
        binding.linearTerm.setOnClickListener {
            showText("Term of us", getString(R.string.term_content))
        }
        binding.switchBar.setOnCheckedChangeListener { _, b ->
            App.dataPer.putBoolean(Params.ON_OFF_NOTIFICATION, b)
        }

        binding.imvPremium.setOnClickListener {
            buyProduct()
        }
    }

    private lateinit var dialogFontSize: Dialog
    private fun showFontSize() {
        dialogFontSize = Dialog(this, R.style.DialogStyle)
        val bindDialog = DialogFontSizeBinding.inflate(LayoutInflater.from(this))
        dialogFontSize.setContentView(bindDialog.root)
        dialogFontSize.setCancelable(true)
        bindDialog.seekbar.progress = App.dataPer.getInt(FONT_SIZE, 0) - 10
        bindDialog.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar, p1: Int, p2: Boolean) {
            }

            override fun onStartTrackingTouch(p0: SeekBar) {
            }

            override fun onStopTrackingTouch(p0: SeekBar) {
                App.dataPer.putInt(FONT_SIZE, p0.progress + 10)
                App.fontSize = p0.progress + 10
            }

        })
        dialogFontSize.show()
    }


    private lateinit var dialogText: Dialog
    private fun showText(title: String, content: String) {
        dialogText = Dialog(this, R.style.ThemeOverlay_AppCompat_DayNight_ActionBar)
        val bindDialog = DialogTextBinding.inflate(LayoutInflater.from(this))
        dialogText.setContentView(bindDialog.root)
        bindDialog.tvTitle.text = title
        bindDialog.textView.text = Html.fromHtml(content)
        bindDialog.imvBack.setOnClickListener {
            dialogText.dismiss()
        }
        dialogText.show()
    }

    private fun buyProduct() {
        if (!InAppManager.instance?.isRemovedAds(this)!!) {
            InAppManager.instance!!.setCallback(this)
            InAppManager.instance!!.purchase(this, Params.PRODUCT_PURCHASE)
        }
    }

    override fun onResume() {
        super.onResume()
        if (InAppManager.instance!!.isRemovedAds(this)) {
            binding.imvPremium.visibility = View.GONE
        } else {
            binding.imvPremium.visibility = View.VISIBLE
        }
    }

    override fun purchaseSuccess() {
        Toast.makeText(this, "You have successfully purchased", Toast.LENGTH_LONG).show()
    }

    override fun purchaseFail() {
        Toast.makeText(this, "Error, purchase failed", Toast.LENGTH_LONG).show()
    }

}