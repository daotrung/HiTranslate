package com.daotrung.hitranslate.utils

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.util.DisplayMetrics
import android.view.LayoutInflater
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.daotrung.hitranslate.App
import com.daotrung.hitranslate.BuildConfig
import com.daotrung.hitranslate.R
import com.daotrung.hitranslate.adapter.ViewLanguageAdapter
import com.daotrung.hitranslate.databinding.DialogShowLanguageBinding
import com.daotrung.hitranslate.databinding.DialogShowTranslateBinding
import com.daotrung.hitranslate.model.ListLanguage
import com.daotrung.hitranslate.model.TranslateHistory
import com.daotrung.hitranslate.utils.TextToSpeechUtil.onStopSpeech
import com.daotrung.hitranslate.utils.widget.CustomToast
import com.google.mlkit.nl.translate.TranslateLanguage


object AppUtils {
    fun getLanguage(): ArrayList<ListLanguage> {
        val arrayList = ArrayList<ListLanguage>()
        // language
        arrayList.add(ListLanguage(R.drawable.flag_af, "Afrikaans", TranslateLanguage.AFRIKAANS))
        arrayList.add(ListLanguage(R.drawable.flag_ar_sa, "Arabic", TranslateLanguage.ARABIC))
        arrayList.add(ListLanguage(R.drawable.flag_be, "Belarusian", TranslateLanguage.BELARUSIAN))
        arrayList.add(ListLanguage(R.drawable.flag_bg, "Bulgarian", TranslateLanguage.BULGARIAN))
        arrayList.add(ListLanguage(R.drawable.flag_bn, "Bengali", TranslateLanguage.BENGALI))
        arrayList.add(ListLanguage(R.drawable.flag_ca, "Catalan", TranslateLanguage.CATALAN))
        arrayList.add(ListLanguage(R.drawable.flag_hmn, "Chinese", TranslateLanguage.CHINESE))
        arrayList.add(ListLanguage(R.drawable.flag_hr, "Croatian", TranslateLanguage.CROATIAN))
        arrayList.add(ListLanguage(R.drawable.flag_cs, "Czech", TranslateLanguage.CZECH))
        arrayList.add(ListLanguage(R.drawable.flag_da, "Danish", TranslateLanguage.DANISH))
        arrayList.add(ListLanguage(R.drawable.flag_nl, "Dutch", TranslateLanguage.DUTCH))
        arrayList.add(ListLanguage(R.drawable.flag_demo, "English", TranslateLanguage.ENGLISH))
        arrayList.add(ListLanguage(R.drawable.flag_eo, "Esperanto", TranslateLanguage.ESPERANTO))
        arrayList.add(ListLanguage(R.drawable.flag_et, "Estonian", TranslateLanguage.ESTONIAN))
        arrayList.add(ListLanguage(R.drawable.flag_fi, "Finnish", TranslateLanguage.FINNISH))
        arrayList.add(ListLanguage(R.drawable.flag_fr_fr, "French", TranslateLanguage.FRENCH))
        arrayList.add(ListLanguage(R.drawable.flag_gl, "Galician", TranslateLanguage.GALICIAN))
        arrayList.add(ListLanguage(R.drawable.flag_ka, "Georgian", TranslateLanguage.GEORGIAN))
        arrayList.add(ListLanguage(R.drawable.flag_de, "German", TranslateLanguage.GERMAN))
        arrayList.add(ListLanguage(R.drawable.flag_el, "Greek", TranslateLanguage.GREEK))
        arrayList.add(ListLanguage(R.drawable.flag_gu, "Gujarati", TranslateLanguage.GUJARATI))
        arrayList.add(ListLanguage(R.drawable.flag_ht, "Haitian", TranslateLanguage.HAITIAN_CREOLE))
        arrayList.add(ListLanguage(R.drawable.flag_yi, "Hebrew", TranslateLanguage.HEBREW))
        arrayList.add(ListLanguage(R.drawable.flag_hi, "Hindi", TranslateLanguage.HINDI))
        arrayList.add(ListLanguage(R.drawable.flag_hu, "Hungarian", TranslateLanguage.HUNGARIAN))
        arrayList.add(ListLanguage(R.drawable.flag_is, "Icelandic", TranslateLanguage.ICELANDIC))
        arrayList.add(ListLanguage(R.drawable.flag_id, "Indonesian", TranslateLanguage.IRISH))
        arrayList.add(ListLanguage(R.drawable.flag_ga, "Irish", TranslateLanguage.IRISH))
        arrayList.add(ListLanguage(R.drawable.flag_it, "Italian", TranslateLanguage.ITALIAN))
        arrayList.add(ListLanguage(R.drawable.flag_ja, "Japanese", TranslateLanguage.JAPANESE))
        arrayList.add(ListLanguage(R.drawable.flag_kn, "Kannada", TranslateLanguage.KANNADA))
        arrayList.add(ListLanguage(R.drawable.flag_lt, "Lithuanian", TranslateLanguage.LITHUANIAN))
        arrayList.add(ListLanguage(R.drawable.flag_lv, "Latvian", TranslateLanguage.LATVIAN))
        arrayList.add(ListLanguage(R.drawable.flag_mk, "Macedonian", TranslateLanguage.MACEDONIAN))
        arrayList.add(ListLanguage(R.drawable.flag_mr, "Marathi", TranslateLanguage.MARATHI))
        arrayList.add(ListLanguage(R.drawable.flag_ms, "Malay", TranslateLanguage.MALAY))
        arrayList.add(ListLanguage(R.drawable.flag_mt, "Maltese", TranslateLanguage.MALTESE))
        arrayList.add(ListLanguage(R.drawable.flag_no, "Norwegian", TranslateLanguage.NORWEGIAN))
        arrayList.add(ListLanguage(R.drawable.flag_fa, "Persian", TranslateLanguage.PERSIAN))
        arrayList.add(ListLanguage(R.drawable.flag_pl, "Polish", TranslateLanguage.POLISH))
        arrayList.add(
            ListLanguage(
                R.drawable.flag_pt_pt,
                "Portuguese",
                TranslateLanguage.PORTUGUESE
            )
        )
        arrayList.add(ListLanguage(R.drawable.flag_ro, "Romanian", TranslateLanguage.ROMANIAN))
        arrayList.add(ListLanguage(R.drawable.flag_ru, "Russian", TranslateLanguage.RUSSIAN))
        arrayList.add(ListLanguage(R.drawable.flag_sk, "Slovak", TranslateLanguage.SLOVAK))
        arrayList.add(ListLanguage(R.drawable.flag_sl, "Slovenian", TranslateLanguage.SLOVENIAN))
        arrayList.add(ListLanguage(R.drawable.flag_es_es, "Spanish", TranslateLanguage.SPANISH))
        arrayList.add(ListLanguage(R.drawable.flag_sv, "Swedish", TranslateLanguage.SWEDISH))
        arrayList.add(ListLanguage(R.drawable.flag_sw, "Swahili", TranslateLanguage.SWAHILI))
        arrayList.add(ListLanguage(R.drawable.flag_tl, "Tagalog", TranslateLanguage.TAGALOG))
        arrayList.add(ListLanguage(R.drawable.flag_ta, "Tamil", TranslateLanguage.TAMIL))
        arrayList.add(ListLanguage(R.drawable.flag_te, "Telugu", TranslateLanguage.TELUGU))
        arrayList.add(ListLanguage(R.drawable.flag_th, "Thai", TranslateLanguage.THAI))
        arrayList.add(ListLanguage(R.drawable.flag_tr, "Turkish", TranslateLanguage.TURKISH))
        arrayList.add(ListLanguage(R.drawable.flag_uk, "Ukrainian", TranslateLanguage.UKRAINIAN))
        arrayList.add(ListLanguage(R.drawable.flag_ur, "Urdu", TranslateLanguage.URDU))
        arrayList.add(ListLanguage(R.drawable.flag_vi, "Vietnamese", TranslateLanguage.VIETNAMESE))
        arrayList.add(ListLanguage(R.drawable.flag_cy, "Welsh", TranslateLanguage.WELSH))
        return arrayList
    }


    //hung
    fun pxToDp(px: Int): Int {
        return (px / Resources.getSystem().displayMetrics.density).toInt()
    }

    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun screenWidth(): Int {
        val displayMetrics: DisplayMetrics = App.context.resources.displayMetrics
        return displayMetrics.widthPixels
    }

    fun screenHeight(): Int {
        val displayMetrics: DisplayMetrics = App.context.resources.displayMetrics
        return displayMetrics.heightPixels
    }

    fun isPackageInstalled(packageName: String?, packageManager: PackageManager): Boolean {
        return try {
            packageManager.getApplicationInfo(packageName!!, 0).enabled
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun Context.checkAppInstall(uri: String): Boolean {
        val pm = packageManager
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
            LogInstance.e(e.message.toString())
        }
        return false
    }


    fun Activity.intentPackage() {
        val appPackageName = "com.google.android.googlequicksearchbox"
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$appPackageName")
                )
            )
        } catch (anfe: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                )
            )
        }
    }

    fun Activity.hasRecordAudioPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkSelfPermission(Manifest.permission.RECORD_AUDIO) ==
                    PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun Context.copyToClipboard(text: CharSequence) {
        onStopSpeech()
        val clipboard: ClipboardManager =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText("", "$text")
        clipboard.setPrimaryClip(clip)
        CustomToast.makeText(
            this,
            "Copy success",
            CustomToast.LENGTH_SHORT,
            CustomToast.SUCCESS,
            true
        ).show()
    }

    fun Context.shareFile(textContent: String?) {
        onStopSpeech()
        try {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name)
            sharingIntent.putExtra(Intent.EXTRA_TEXT, "$textContent")
            startActivity(Intent.createChooser(sharingIntent, "Share to"))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun Activity.shareApplicationId() {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            val shareContent =
                "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent)
            startActivity(Intent.createChooser(shareIntent, "Go to link :  "))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun Activity.writeReview() {
        val applicationNameId: String = BuildConfig.APPLICATION_ID
        val uri: Uri = Uri.parse("market://details?id=$applicationNameId")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        goToMarket.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        )
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=$applicationNameId")
                )
            )
        }
    }

    fun Context.sendSupportGmail() {
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "message/rfc822"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("suntectltd.software@gmail.com"))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
        try {
            emailIntent.setPackage("com.google.android.gm")
            startActivity(emailIntent)
        } catch (e: Exception) {
        }

    }


    private lateinit var dialogText: Dialog
    fun Context.showText(item: TranslateHistory) {
        dialogText = Dialog(this, R.style.ThemeOverlay_AppCompat_DayNight_ActionBar)
        val bindDialog = DialogShowTranslateBinding.inflate(LayoutInflater.from(this))
        dialogText.setContentView(bindDialog.root)
        bindDialog.imvFlagFrom.setImageResource(item.imgFlagIn)
        bindDialog.imvFlagTo.setImageResource(item.imgFlagOut)
        bindDialog.tvInput.apply {
            text = item.txtIn
            textSize = App.fontSize.toFloat()
        }
        bindDialog.tvOutput.apply {
            text = item.txtOut
            textSize = App.fontSize.toFloat()
        }
        bindDialog.imgDelete.setOnClickListener {
            App.getDb().appDao().deleteHistory(item)
            CustomToast.makeText(
                this,
                "Delete successful",
                CustomToast.LENGTH_SHORT,
                CustomToast.SUCCESS,
                true
            ).show()
            dialogText.dismiss()
        }
        bindDialog.imgBack.setOnClickListener {
            dialogText.dismiss()
        }
        dialogText.show()
    }


    private lateinit var dialogLanguage: Dialog

    fun Context.showLanguage(adapterLanguage: ViewLanguageAdapter) {
        onStopSpeech()
        adapterLanguage.setData(getLanguage())
        dialogLanguage = Dialog(this, android.R.style.Theme_NoTitleBar_Fullscreen)
        val bindDialog = DialogShowLanguageBinding.inflate(LayoutInflater.from(this))
        dialogLanguage.setContentView(bindDialog.root)
        val mLayout = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        bindDialog.rcvLanguage.layoutManager = mLayout
        bindDialog.rcvLanguage.adapter = adapterLanguage
        bindDialog.ivClose.setOnClickListener {
            dialogLanguage.dismiss()
        }
        bindDialog.search.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                adapterLanguage.filter.filter(p0.toString().trim())
                return false
            }
        })
        dialogLanguage.show()
    }

    fun dialogDismis() {
        dialogLanguage.dismiss()
    }
}