package com.daotrung.hitranslate.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.speech.SpeechRecognizer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.daotrung.hitranslate.App
import com.daotrung.hitranslate.R
import com.daotrung.hitranslate.adapter.ViewLanguageAdapter
import com.daotrung.hitranslate.base.BaseFragment
import com.daotrung.hitranslate.base.Params
import com.daotrung.hitranslate.databinding.FragmentVoiceTranslateBinding
import com.daotrung.hitranslate.model.ListLanguage
import com.daotrung.hitranslate.model.TranslateHistory
import com.daotrung.hitranslate.state.LanguageSate
import com.daotrung.hitranslate.state.RecordingState
import com.daotrung.hitranslate.utils.*
import com.daotrung.hitranslate.utils.AppUtils.copyToClipboard
import com.daotrung.hitranslate.utils.AppUtils.hasRecordAudioPermission
import com.daotrung.hitranslate.utils.AppUtils.intentPackage
import com.daotrung.hitranslate.utils.AppUtils.isPackageInstalled
import com.daotrung.hitranslate.utils.TextToSpeechUtil.onStopSpeech
import com.daotrung.hitranslate.utils.AppUtils.shareFile
import com.daotrung.hitranslate.utils.AppUtils.showLanguage
import com.daotrung.hitranslate.utils.TextToSpeechUtil.covertTextToSpeech
import com.daotrung.hitranslate.utils.admob.InterstitialManager
import com.daotrung.hitranslate.utils.interfaces.ItemListener
import com.daotrung.hitranslate.utils.widget.CustomToast
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateLanguage
import java.util.*


class VoiceTranslateFragment : BaseFragment(), ItemListener {
    private lateinit var binding: FragmentVoiceTranslateBinding
    private var speechRecognizer: SpeechRecognizer? = null
    private var micState = RecordingState.IDLE
    private var languageState = LanguageSate.FROM
    private lateinit var languageTo: String
    private lateinit var languageForm: String
    private lateinit var listLanguage: ArrayList<ListLanguage>
    private lateinit var adapterLanguage: ViewLanguageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVoiceTranslateBinding.inflate(
            inflater, container, false
        )
        binding.tvInput.textSize = App.fontSize.toFloat()
        binding.tvOutput.textSize = App.fontSize.toFloat()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!mActivity.hasRecordAudioPermission()) {
            requestPermission(Manifest.permission.RECORD_AUDIO)
        }
        requireActivity().window.statusBarColor = Color.rgb(71, 124, 246)
        languageTo = App.dataPer.getString(Params.KEY_LANGUAGE_TO, "").toString()
        languageForm = App.dataPer.getString(Params.KEY_LANGUAGE_FROM, "").toString()
        initView()
        setOnClickListener()
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
                    App.dataPer.putString(Params.KEY_LANGUAGE_FROM, it.txtShortNameLanguage)
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
                    App.dataPer.putString(Params.KEY_LANGUAGE_TO, TranslateLanguage.ENGLISH)
                    binding.imvFlag.tag = R.drawable.flag_demo
                } else if (it.txtShortNameLanguage != Locale.getDefault().language) {
                    binding.imvFlag.setImageResource(it.imgFlag)
                    binding.tvOutputLanguage.text = " : ${it.txtNameLanguage}"
                    App.dataPer.putString(Params.KEY_LANGUAGE_TO, it.txtShortNameLanguage)
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
    }

    private lateinit var progressdialog: ProgressDialog
    private fun setOnClickListener() {
        binding.imvBack.setOnClickListener {
            onBack()
        }
        binding.imgMicAnimation.setOnClickListener {
            onStopSpeech()
            if (mActivity.hasRecordAudioPermission()) {
                if (isPackageInstalled(
                        "com.google.android.googlequicksearchbox",
                        mContext.packageManager
                    )
                ) {
                    try {
                        if (SpeechRecognizer.isRecognitionAvailable(requireContext())) {
                            binding.rippleBackground.startRippleAnimation()
                            if (micState == RecordingState.IDLE) {
                                micState = RecordingState.RECORDING
                                speechRecognizer =
                                    SpeechRecognitionUtil.setupSpeechRecognizer(mContext,
                                        onReadyForSpeech = {
                                            binding.tvInput.hint = "Listening......"
                                        },
                                        onPartialResults = {
                                            binding.tvInput.text =
                                                it.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)!![0]
                                                    ?: ""
                                        },
                                        onResults = {
                                            binding.tvInput.text =
                                                it.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)!![0]
                                                    ?: ""
                                            speechRecognizer?.stopListening()
                                            binding.imvFavorite.setImageResource(R.drawable.ic_favorite_select)
                                            identifyLanguageWithStringInput(binding.tvInput.text.toString())
                                        },
                                        onError = {
                                            speechRecognizer?.startListening(
                                                SpeechRecognitionUtil.getRecognizerIntent(
                                                    App.dataPer.getString(
                                                        Params.KEY_LANGUAGE_FROM,
                                                        ""
                                                    )
                                                        .toString()
                                                )
                                            )
                                        })
//                        startActivityForResult(SpeechRecognitionUtil.speechRecognizerIntent, 100)
                                speechRecognizer?.startListening(
                                    SpeechRecognitionUtil.getRecognizerIntent(
                                        App.dataPer.getString(Params.KEY_LANGUAGE_FROM, "")
                                            .toString()
                                    )
                                )
                            } else {
                                speechRecognizer?.stopListening()
                                speechRecognizer = null
                                micState = RecordingState.IDLE
                                binding.rippleBackground.stopRippleAnimation()
                            }
                        } else alterDialog()
                    } catch (e: Exception) {
                        alterDialog()
                    }
                } else alterDialog()
            }
        }
        binding.icSpeakTextFrom.setOnClickListener {
            try {
                if (TextToSpeechUtil.textToSpeech?.isSpeaking == true) {
                    onStopSpeech()
                } else {
                    requireActivity().covertTextToSpeech(
                        binding.tvInput.text.toString().trim(),
                        App.dataPer.getString(Params.KEY_LANGUAGE_FROM, "").toString()
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
                        App.dataPer.getString(Params.KEY_LANGUAGE_TO, "").toString()
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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
        binding.imvFavorite.setOnClickListener {
            onStopSpeech()
            val txtInt = binding.tvInput.text.toString().trim()
            val txtOut = binding.tvOutput.text.toString().trim()
            if (txtOut.isNotEmpty()) {
                if (!App.getDb().appDao().isExistTranslate(txtOut, txtInt)) {
                    val model = model(txtInt, txtOut)
                    model.isFavorite = true
                    App.getDb().appDao().insertTranslate(model)
                } else App.getDb().appDao().updateFavorite(true, txtOut, txtInt)
                binding.imvFavorite.setImageResource(R.drawable.ic_favorite)
            } else showToast()
        }
    }

    private fun model(txtInt: String, txtOut: String): TranslateHistory {
        return TranslateHistory(
            0,
            binding.imvFlagFrom.tag as Int,
            txtInt,
            binding.imvFlag.tag as Int,
            txtOut,
            App.dataPer.getString(Params.KEY_LANGUAGE_TO, "").toString(),
            App.dataPer.getString(Params.KEY_LANGUAGE_FROM, "").toString()
        )
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

    private fun onTextTranslate() {
        if (binding.tvInput.text.toString().isNotEmpty()) {
            TranslationUtils.onTranslation(
                App.dataPer.getString(Params.KEY_LANGUAGE_FROM, "")
                    .toString(),
                App.dataPer.getString(Params.KEY_LANGUAGE_TO, "")
                    .toString(),
                binding.tvInput.text.toString().trim(),
                object :
                    TranslationUtils.OnTranslationCompleteListener {
                    override fun onStartTranslation() {
                        progressdialog = ProgressDialog(mContext)
                        progressdialog.setMessage(getString(R.string.please_wait))
                        progressdialog.show()
                        progressdialog.setCancelable(false)
                    }

                    override fun onCompleted(text: String?) {
                        binding.tvOutput.text = text.toString()
                        progressdialog.dismiss()
                        progressdialog.hide()
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
                            App.dataPer.getString(Params.KEY_LANGUAGE_FROM, "")
                                .toString(),
                            App.dataPer.getString(Params.KEY_LANGUAGE_TO, "")
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
                                    Toast.makeText(
                                        mContext,
                                        "Error ${e?.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            }
                        )
                    }

                }
            )
        } else CustomToast.makeText(
            mContext,
            "Please text voice translate",
            CustomToast.LENGTH_SHORT,
            CustomToast.ERROR,
            true
        ).show()
    }

    private fun alterDialog() {
        val builderDialog = AlertDialog.Builder(mContext)
        builderDialog.setMessage(getString(R.string.not_find_google))
        builderDialog.setPositiveButton(getString(R.string.install)) { dialog: DialogInterface, _: Int ->
            mActivity.intentPackage()
            dialog.dismiss()
        }
        builderDialog.setNegativeButton("No") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }
        builderDialog.create().show()
    }

    @SuppressLint("SetTextI18n")
    override fun itemListener(item: Any) {
        if (item is ListLanguage) {
            when (languageState) {
                LanguageSate.TO -> {
                    binding.imvFlag.setImageResource(item.imgFlag)
                    binding.imvFlag.tag = item.imgFlag
                    binding.tvOutputLanguage.text = " : ${item.txtNameLanguage}"
                    App.dataPer.putString(Params.KEY_LANGUAGE_TO, item.txtShortNameLanguage)
                    identifyLanguageWithStringInput(binding.tvInput.text.toString())
                    binding.imvFavorite.setImageResource(R.drawable.ic_favorite_select)
                }
                LanguageSate.FROM -> {
                    binding.imvFlagFrom.setImageResource(item.imgFlag)
                    binding.imvFlagFrom.tag = item.imgFlag
                    binding.tvInputLanguage.text = " : ${item.txtNameLanguage}"
                    App.dataPer.putString(Params.KEY_LANGUAGE_FROM, item.txtShortNameLanguage)
                    binding.imvFavorite.setImageResource(R.drawable.ic_favorite_select)
                }
            }
            AppUtils.dialogDismis()
        }
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
                                Params.KEY_LANGUAGE_FROM,
                                it.txtShortNameLanguage
                            )
                        }
                    }
                    onTextTranslate()
                } else {
                    LogInstance.e("not found")
                    onTextTranslate()
                }
            }
            .addOnFailureListener {
                LogInstance.e("exception processing $it")
                onTextTranslate()
            }
    }

    override fun onDestroy() {
        onStopSpeech()
        if (speechRecognizer != null) {
            speechRecognizer!!.destroy()
            speechRecognizer = null
        }
        super.onDestroy()
    }

    override fun onDestroyView() {
        onStopSpeech()
        super.onDestroyView()
    }
}