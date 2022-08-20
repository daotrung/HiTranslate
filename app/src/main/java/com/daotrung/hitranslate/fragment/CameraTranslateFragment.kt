package com.daotrung.hitranslate.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.util.Linkify
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.core.widget.addTextChangedListener
import com.daotrung.hitranslate.App
import com.daotrung.hitranslate.R
import com.daotrung.hitranslate.adapter.ViewLanguageAdapter
import com.daotrung.hitranslate.base.BaseFragment
import com.daotrung.hitranslate.base.Params
import com.daotrung.hitranslate.databinding.FragmentCameraTranslateBinding
import com.daotrung.hitranslate.model.ListLanguage
import com.daotrung.hitranslate.model.TranslateHistory
import com.daotrung.hitranslate.state.LanguageSate
import com.daotrung.hitranslate.utils.AppUtils
import com.daotrung.hitranslate.utils.AppUtils.copyToClipboard
import com.daotrung.hitranslate.utils.AppUtils.shareFile
import com.daotrung.hitranslate.utils.AppUtils.showLanguage
import com.daotrung.hitranslate.utils.LogInstance
import com.daotrung.hitranslate.utils.TextToSpeechUtil
import com.daotrung.hitranslate.utils.TextToSpeechUtil.covertTextToSpeech
import com.daotrung.hitranslate.utils.TextToSpeechUtil.onStopSpeech
import com.daotrung.hitranslate.utils.TranslationUtils
import com.daotrung.hitranslate.utils.admob.InterstitialManager
import com.daotrung.hitranslate.utils.interfaces.ItemListener
import com.daotrung.hitranslate.utils.widget.CustomToast
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.*


class CameraTranslateFragment : BaseFragment(), ItemListener {

    private lateinit var binding: FragmentCameraTranslateBinding
    private lateinit var recognizer: TextRecognizer
    private lateinit var adapterLanguage: ViewLanguageAdapter
    private var languageState = LanguageSate.FROM
    private lateinit var languageTo: String
    private lateinit var languageForm: String
    private lateinit var listLanguage: ArrayList<ListLanguage>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        binding = FragmentCameraTranslateBinding.inflate(inflater, container, false)
        binding.tvInput.textSize = App.fontSize.toFloat()
        binding.tvOutput.textSize = App.fontSize.toFloat()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.statusBarColor = Color.rgb(71, 124, 246)
        binding.imvBack.setOnClickListener {
            onBack()
        }
        binding.imgCamera.setOnClickListener {
            onStopSpeech()
            ImagePicker.with(this).crop().start()
        }
        languageTo = App.dataPer.getString(Params.KEY_LANGUAGE_TO_CAMERA, "").toString()
        languageForm = App.dataPer.getString(Params.KEY_LANGUAGE_FROM_CAMERA, "").toString()
        initView()
        binding.done.setOnClickListener {
            binding.textInImageLayout.visibility = View.GONE
            val bitmap = binding.imagePreview.drawable.toBitmap()
            runTextRecognition(bitmap)
        }
        binding.close.setOnClickListener {
            binding.textInImageLayout.visibility = View.GONE
        }
    }


    @SuppressLint("SetTextI18n")
    private fun initView() {
        listLanguage = AppUtils.getLanguage()
        listLanguage.forEach {
            if (languageForm.isEmpty()) {
                if (it.txtShortNameLanguage == Locale.getDefault().language) {
                    binding.imvFlagFrom.tag = it.imgFlag
                    binding.imvFlagFrom.setImageResource(it.imgFlag)
                    binding.tvInputLanguage.text = " : ${it.txtNameLanguage}"
                    App.dataPer.putString(Params.KEY_LANGUAGE_FROM_CAMERA, it.txtShortNameLanguage)
                }
            } else {
                if (languageForm == it.txtShortNameLanguage) {
                    binding.imvFlagFrom.setImageResource(it.imgFlag)
                    binding.tvInputLanguage.text = " : ${it.txtNameLanguage}"
                    binding.imvFlagFrom.tag = it.imgFlag
                }
            }

            if (languageTo.isEmpty()) {
                if (Locale.getDefault().language != "en") {
                    binding.imvFlag.setImageResource(R.drawable.flag_demo)
                    binding.tvOutputLanguage.text = " : English"
                    App.dataPer.putString(Params.KEY_LANGUAGE_TO_CAMERA, TranslateLanguage.ENGLISH)
                    binding.imvFlag.tag = R.drawable.flag_demo
                } else if (it.txtShortNameLanguage != Locale.getDefault().language) {
                    binding.imvFlag.setImageResource(it.imgFlag)
                    binding.tvOutputLanguage.text = " : ${it.txtNameLanguage}"
                    App.dataPer.putString(Params.KEY_LANGUAGE_TO_CAMERA, it.txtShortNameLanguage)
                    binding.imvFlag.tag = it.imgFlag
                }
            } else {
                if (languageTo == it.txtShortNameLanguage) {
                    binding.imvFlag.setImageResource(it.imgFlag)
                    binding.tvOutputLanguage.text = " : ${it.txtNameLanguage}"
                    binding.imvFlag.tag = it.imgFlag
                }
            }
        }
        adapterLanguage = ViewLanguageAdapter(this)

        binding.imvDropFrom.setOnClickListener {
            languageState = LanguageSate.FROM
            mContext.showLanguage(adapterLanguage)
        }
        binding.imvDropTo.setOnClickListener {
            languageState = LanguageSate.TO
            mContext.showLanguage(adapterLanguage)
        }
        binding.imvCopyTo.setOnClickListener {
            if (binding.tvOutput.text.trim().isNotEmpty()) {
                mContext.copyToClipboard(binding.tvOutput.text.trim())
            } else showToast()
        }
        binding.imvCopyFrom.setOnClickListener {
            if (binding.tvInput.text.trim().isNotEmpty()) {
                mContext.copyToClipboard(binding.tvInput.text.trim())
            } else showToast()
        }
        binding.imvShare.setOnClickListener {
            if (binding.tvOutput.text.trim().isNotEmpty()) {
                mContext.shareFile(binding.tvOutput.text.toString().trim())
            } else showToast()
        }
        binding.icSpeakTextFrom.setOnClickListener {
            try {
                if (TextToSpeechUtil.textToSpeech?.isSpeaking == true) {
                    onStopSpeech()
                } else {
                    requireActivity().covertTextToSpeech(
                        binding.tvInput.text.toString().trim(),
                        App.dataPer.getString(Params.KEY_LANGUAGE_FROM_CAMERA, "").toString()
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding.icSpeakTextTo.setOnClickListener {
            try {
                if (TextToSpeechUtil.textToSpeech?.isSpeaking == true) {
                    onStopSpeech()
                } else {
                    requireActivity().covertTextToSpeech(
                        binding.tvOutput.text.toString().trim(),
                        App.dataPer.getString(Params.KEY_LANGUAGE_TO_CAMERA, "").toString()
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding.imvFavorite.setOnClickListener {
            onStopSpeech()
            val txtInt = binding.tvInput.text.toString().trim()
            val txtOut = binding.tvOutput.text.toString().trim()
            if (txtOut.isNotEmpty()) {
                val model = model(txtInt, txtOut)
                if (!App.getDb().appDao().isExistTranslate(txtOut, txtInt)) {
                    model.isFavorite = true
                    App.getDb().appDao().insertTranslate(model)
                } else App.getDb().appDao().updateFavorite(true, txtOut, txtInt)
                binding.imvFavorite.setImageResource(R.drawable.ic_favorite)
            } else showToast()
        }
        binding.tvInput.addTextChangedListener {
            binding.imvFavorite.setImageResource(R.drawable.ic_favorite_select)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                val uri: Uri = data?.data!!
                binding.textInImageLayout.visibility = View.VISIBLE
                binding.imagePreview.apply {
                    visibility = View.VISIBLE
                    setImageURI(uri)
                }
            }
            ImagePicker.RESULT_ERROR -> {
                LogInstance.e(ImagePicker.getError(data))
            }
            else -> {
                LogInstance.e("No Image Selected")
            }
        }
    }

    private val TAG = "Testing"
    private fun runTextRecognition(bitmap: Bitmap) {
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        recognizer
            .process(inputImage)
            .addOnSuccessListener { text ->
                processTextRecognitionResult(text)
            }.addOnFailureListener { e ->
                e.printStackTrace()
                Toast.makeText(mActivity, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private lateinit var progressdialog: ProgressDialog
    private fun processTextRecognitionResult(result: Text) {
        var finalText = ""
        LogInstance.e(result)
        for (block in result.textBlocks) {
            for (line in block.lines) {
                finalText += line.text + " \n"
            }
            finalText += "\n"
        }
        LogInstance.e(result.text)
        LogInstance.e(finalText)
        if (finalText.isNotEmpty()) {
            identifyLanguageWithStringInput(finalText.trim())
        }
        binding.tvInput.setText(
            if (finalText.isNotEmpty()) {
                finalText
            } else {
                getString(R.string.no_text_found)
            }
        )
        Linkify.addLinks(binding.tvInput, Linkify.ALL)
    }

    private fun onTextTranslate(text: String) {
        TranslationUtils.onTranslation(
            App.dataPer.getString(Params.KEY_LANGUAGE_FROM_CAMERA, "")
                .toString(),
            App.dataPer.getString(Params.KEY_LANGUAGE_TO_CAMERA, "")
                .toString(),
            text,
            object :
                TranslationUtils.OnTranslationCompleteListener {
                override fun onStartTranslation() {
                    progressdialog = ProgressDialog(mContext)
                    progressdialog.setMessage(getString(R.string.please_wait))
                    progressdialog.show()
                    progressdialog.setCancelable(false)
                }

                override fun onCompleted(text: String?) {
                    progressdialog.dismiss()
                    progressdialog.hide()
                    binding.tvOutput.text = text.toString()
                    val txtInt = binding.tvInput.text.toString().trim()
                    val txtOut = binding.tvOutput.text.toString().trim()
                    val model = model(txtInt, txtOut)
                    if (!App.getDb().appDao().isExistTranslate(txtOut, txtInt)) {
                        App.getDb().appDao().insertTranslate(model)
                    }
                }

                override fun onError(e: Exception?) {
                    TranslationUtils.getTranslatorOptions(
                        binding.tvInput.text.toString().trim(),
                        App.dataPer.getString(Params.KEY_LANGUAGE_FROM_CAMERA, "")
                            .toString(),
                        App.dataPer.getString(Params.KEY_LANGUAGE_TO_CAMERA, "")
                            .toString(),
                        object : TranslationUtils.OnTranslationCompleteListener {
                            override fun onStartTranslation() {
                            }

                            override fun onCompleted(text: String?) {
                                progressdialog.dismiss()
                                progressdialog.hide()
                                binding.tvOutput.text = text
                                val txtInt = binding.tvInput.text.toString().trim()
                                val txtOut = binding.tvOutput.text.toString().trim()
                                val model = model(txtInt, txtOut)
                                if (!App.getDb().appDao().isExistTranslate(txtOut, txtInt)) {
                                    App.getDb().appDao().insertTranslate(model)
                                }
                            }

                            override fun onError(e: Exception?) {
                                progressdialog.dismiss()
                                progressdialog.hide()
                                Toast.makeText(mContext, "Error ${e?.message}", Toast.LENGTH_SHORT)
                                    .show()
                                LogInstance.e(e?.message.toString())
                            }

                        }
                    )
                }
            }
        )
    }

    private fun model(txtInt: String, txtOut: String): TranslateHistory {
        return TranslateHistory(
            0,
            binding.imvFlagFrom.tag as Int,
            txtInt,
            binding.imvFlag.tag as Int,
            txtOut,
            App.dataPer.getString(Params.KEY_LANGUAGE_TO_CAMERA, "").toString(),
            App.dataPer.getString(Params.KEY_LANGUAGE_FROM_CAMERA, "").toString()
        )
    }

    override fun itemListener(item: Any) {
        if (item is ListLanguage) {
            when (languageState) {
                LanguageSate.TO -> {
                    binding.imvFlag.setImageResource(item.imgFlag)
                    binding.imvFlag.tag = item.imgFlag
                    binding.tvOutputLanguage.text = " : ${item.txtNameLanguage}"
                    App.dataPer.putString(Params.KEY_LANGUAGE_TO_CAMERA, item.txtShortNameLanguage)
                    if (binding.tvInput.text.isNotEmpty()) {
                        onTextTranslate(binding.tvInput.text.toString().trim())
                    } else showToast()
                }

                LanguageSate.FROM -> {
                    binding.imvFlagFrom.setImageResource(item.imgFlag)
                    binding.imvFlagFrom.tag = item.imgFlag
                    binding.tvInputLanguage.text = " : ${item.txtNameLanguage}"
                    App.dataPer.putString(
                        Params.KEY_LANGUAGE_FROM_CAMERA,
                        item.txtShortNameLanguage
                    )
                }
            }
            AppUtils.dialogDismis()
        }
    }

    private fun showToast() {
        CustomToast.makeText(
            mContext,
            "Content not available",
            CustomToast.LENGTH_SHORT,
            CustomToast.ERROR,
            true
        ).show()
    }

    private fun identifyLanguageWithStringInput(text: String) {
        val languageIdentifier = LanguageIdentification.getClient()
        languageIdentifier.identifyLanguage(text)
            .addOnSuccessListener { languageCode ->
                if (languageCode != "und") {
                    LogInstance.e(languageCode)
                    listLanguage.forEach {
                        if (it.txtShortNameLanguage == languageCode) {
                            binding.imvFlagFrom.tag = it.imgFlag
                            binding.imvFlagFrom.setImageResource(it.imgFlag)
                            binding.tvInputLanguage.text = " : ${it.txtNameLanguage}"
                            App.dataPer.putString(
                                Params.KEY_LANGUAGE_FROM_CAMERA,
                                it.txtShortNameLanguage
                            )
                        }
                    }
                    onTextTranslate(text.trim())
                } else {
                    LogInstance.e("not found")
                    onTextTranslate(text.trim())
                }
            }
            .addOnFailureListener {
                LogInstance.e("exception processing $it")
                onTextTranslate(text.trim())
            }
    }

    override fun onDestroy() {
        onStopSpeech()
        super.onDestroy()
    }

    override fun onDestroyView() {
        onStopSpeech()
        super.onDestroyView()
    }
}