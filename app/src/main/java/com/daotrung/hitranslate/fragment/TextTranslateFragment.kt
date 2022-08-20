package com.daotrung.hitranslate.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.*
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.daotrung.hitranslate.App
import com.daotrung.hitranslate.R
import com.daotrung.hitranslate.adapter.SpinnerChooseLanguageAdapter
import com.daotrung.hitranslate.base.BaseFragment
import com.daotrung.hitranslate.base.Params
import com.daotrung.hitranslate.databinding.FragmentTextTranslateBinding
import com.daotrung.hitranslate.model.ListLanguage
import com.daotrung.hitranslate.model.TranslateHistory
import com.daotrung.hitranslate.utils.AppUtils
import com.daotrung.hitranslate.utils.AppUtils.shareFile
import com.daotrung.hitranslate.utils.LogInstance
import com.daotrung.hitranslate.utils.TextToSpeechUtil
import com.daotrung.hitranslate.utils.TextToSpeechUtil.covertTextToSpeech
import com.daotrung.hitranslate.utils.TextToSpeechUtil.onStopSpeech
import com.daotrung.hitranslate.utils.TranslationUtils
import com.daotrung.hitranslate.utils.widget.CustomToast
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateLanguage
import java.util.*


class TextTranslateFragment : BaseFragment() {
    private lateinit var binding: FragmentTextTranslateBinding
    private lateinit var imgArrowBackText: ImageView
    private lateinit var edtTextTranslate: EditText
    private lateinit var txtDisplay: TextView
    private lateinit var txtLanguageInput: TextView
    private lateinit var txtLanguageOutput: TextView
    private var originalString: String = ""
    private lateinit var edtSearchViewSpinner: EditText
    private lateinit var arrayList: ArrayList<ListLanguage>
    private lateinit var rvChooseLanguage: RecyclerView
    private lateinit var spinnerAdapter: SpinnerChooseLanguageAdapter
    private lateinit var dialog: Dialog

    // progress bar dialog
    private lateinit var pDialog: SweetAlertDialog
    private lateinit var btnDropTextLanguageIn: LinearLayout
    private lateinit var btnDropTextLanguageOut: LinearLayout

    // flag
    private lateinit var flagInputTextTranslate: ImageView
    private lateinit var flagOutputTextTranslate: ImageView
    private lateinit var imgFlagTxtIn: ImageView
    private lateinit var imgFlagTxtOut: ImageView

    // item language
    private var itemInLanguage: ListLanguage? = null
    private var itemOuLanguage: ListLanguage? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mActivity.window.statusBarColor = Color.rgb(255, 255, 255)
        binding = FragmentTextTranslateBinding.inflate(inflater, container, false)
        binding.edtInputText.textSize = App.fontSize.toFloat()
        binding.txtDisplayOutputText.textSize = App.fontSize.toFloat()
        // initId
        arrayList = ArrayList()
        arrayList = AppUtils.getLanguage()

        imgArrowBackText = binding.arrowBackText
        edtTextTranslate = binding.edtInputText
        txtDisplay = binding.txtDisplayOutputText
        btnDropTextLanguageIn = binding.btnDropTextLanguageIn
        btnDropTextLanguageOut = binding.btnDropTextLanguageOut
        txtLanguageInput = binding.txtLanguageInput
        txtLanguageOutput = binding.txtLanguageOutput
        flagOutputTextTranslate = binding.flagOutputTextTranslate
        flagInputTextTranslate = binding.flagInputTextTranslate
        imgFlagTxtIn = binding.imgFlagTxtIn
        imgFlagTxtOut = binding.imgFlagTxtOut


        binding.edtInputText.apply {
            setRawInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
            setImeActionLabel(
                "DONE",
                EditorInfo.IME_ACTION_DONE
            )
            imeOptions = EditorInfo.IME_ACTION_DONE
        }
        // set up progress dialog
        setUpProgressDialog()
        // return arrow toolbar
        imgArrowBackText.setOnClickListener {
            onBack()
        }
        // get edittext to translate
        edtTextTranslate.setOnEditorActionListener(OnEditorActionListener { _, actionId, _ ->
            onStopSpeech()
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (edtTextTranslate.text.toString() != "") {
                    originalString = edtTextTranslate.text.toString().trim()
                    identifyLanguageWithStringInput(originalString)
                } else {
                    Toast.makeText(requireContext(), "No data input", Toast.LENGTH_SHORT).show()
                }

                return@OnEditorActionListener true
            }
            false
        })
        // choose language in
        btnDropTextLanguageIn.setOnClickListener {
            onStopSpeech()
            dialog = Dialog(this.requireContext())
            // set custom dialog
            dialog.setContentView(R.layout.dialog_searchable_spinner)
            // set custom height and width
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // set transparent background
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            // show dialog
            dialog.show()

            // init Id
            edtSearchViewSpinner = dialog.findViewById(R.id.edit_text)
            rvChooseLanguage = dialog.findViewById(R.id.rvListChooseLanguage)
            spinnerAdapter = SpinnerChooseLanguageAdapter(arrayList)

            rvChooseLanguage.addItemDecoration(
                DividerItemDecoration(
                    view?.context,
                    LinearLayoutManager.VERTICAL
                )
            )
            rvChooseLanguage.layoutManager = LinearLayoutManager(view?.context)
            rvChooseLanguage.adapter = spinnerAdapter

            dialog.findViewById<ImageView>(R.id.close_dialog_search).setOnClickListener {
                dialog.dismiss()
            }
            edtSearchViewSpinner.addTextChangedListener {
                setFilter(it.toString())
            }
            spinnerAdapter.setOnItemClickListener(object :
                SpinnerChooseLanguageAdapter.onItemClickListener {
                override fun onItemClick(position: Int, item: ListLanguage) {
                    dialog.dismiss()
                    setFromLanguage(item)
                    binding.iconHeart.setImageResource(R.drawable.icon_heart_unselect_text)
                }

            })

        }
        // choose language out
        btnDropTextLanguageOut.setOnClickListener {
            onStopSpeech()
            dialog = Dialog(this.requireContext())
            // set custom dialog
            dialog.setContentView(R.layout.dialog_searchable_spinner)
            // set custom height and width
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // set transparent background
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            // show dialog
            dialog.show()

            // init Id
            edtSearchViewSpinner = dialog.findViewById(R.id.edit_text)
            rvChooseLanguage = dialog.findViewById(R.id.rvListChooseLanguage)
            spinnerAdapter = SpinnerChooseLanguageAdapter(arrayList)

            rvChooseLanguage.addItemDecoration(
                DividerItemDecoration(
                    view?.context,
                    LinearLayoutManager.VERTICAL
                )
            )
            rvChooseLanguage.adapter = spinnerAdapter
            rvChooseLanguage.layoutManager = LinearLayoutManager(view?.context)

            dialog.findViewById<ImageView>(R.id.close_dialog_search).setOnClickListener {
                dialog.dismiss()
            }
            edtSearchViewSpinner.addTextChangedListener {
                setFilter(it.toString())
            }
            spinnerAdapter.setOnItemClickListener(object :
                SpinnerChooseLanguageAdapter.onItemClickListener {
                override fun onItemClick(position: Int, item: ListLanguage) {
                    setToLanguage(item)
                    dialog.dismiss()
                    binding.iconHeart.setImageResource(R.drawable.icon_heart_unselect_text)
                    originalString = edtTextTranslate.text.toString().trim()
                    if (originalString.isNotEmpty()) {
                        identifyLanguageWithStringInput(originalString)
                    }
                }

            })

        }

        // set text to speech
        binding.imgVolumeInputText.setOnClickListener {
            try {
                if (TextToSpeechUtil.textToSpeech?.isSpeaking == true) {
                    onStopSpeech()
                } else {
                    requireActivity().covertTextToSpeech(
                        binding.edtInputText.text.toString().trim(),
                        App.dataPer.getString(Params.KEY_LANGUAGE_FROM_TEXT, "").toString()
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding.imgVolumeOutputText.setOnClickListener {
            try {
                if (TextToSpeechUtil.textToSpeech?.isSpeaking == true) {
                    onStopSpeech()
                } else {
                    requireActivity().covertTextToSpeech(
                        binding.txtDisplayOutputText.text.toString().trim(),
                        App.dataPer.getString(Params.KEY_LANGUAGE_TO_TEXT, "").toString()
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // set copy text
        binding.imgCopyOutText.setOnClickListener {
            onStopSpeech()
            (requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).apply {
                setPrimaryClip(ClipData.newPlainText("Copied text", txtDisplay.text.toString()))
            }
            Toast.makeText(context, "Copied Text", Toast.LENGTH_SHORT).show()

        }
        binding.iconShare.setOnClickListener {
            if (binding.txtDisplayOutputText.text.trim().isNotEmpty()) {
                view?.context!!.shareFile(binding.txtDisplayOutputText.text.toString().trim())
            } else CustomToast.makeText(
                view?.context,
                "Content not available",
                CustomToast.LENGTH_SHORT,
                CustomToast.ERROR,
                true
            ).show()
        }
        binding.iconHeart.setOnClickListener {
            onStopSpeech()
            val txtInt = binding.edtInputText.text.toString().trim()
            val txtOut = binding.txtDisplayOutputText.text.toString().trim()
            if (item == null) {
                if (txtOut.isNotEmpty()) {
                    val model = getModel(txtInt, txtOut)
                    if (isCheck) {
                        isCheck = false
                        if (!App.getDb().appDao().isExistTranslate(txtOut, txtInt)) {
                            model.isFavorite = false
                            App.getDb().appDao().insertTranslate(model)
                        } else App.getDb().appDao().updateFavorite(false, txtOut, txtInt)
                        binding.iconHeart.setImageResource(R.drawable.icon_heart_unselect_text)
                        CustomToast.makeText(
                            mContext,
                            "Un favorite success",
                            CustomToast.LENGTH_SHORT,
                            CustomToast.SUCCESS,
                            true
                        ).show()
                    } else {
                        isCheck = true
                        if (!App.getDb().appDao().isExistTranslate(txtOut, txtInt)) {
                            model.isFavorite = true
                            App.getDb().appDao().insertTranslate(model)
                        } else App.getDb().appDao().updateFavorite(true, txtOut, txtInt)
                        binding.iconHeart.setImageResource(R.drawable.ic_favorite_text)
                        CustomToast.makeText(
                            mContext,
                            "Favorite success",
                            CustomToast.LENGTH_SHORT,
                            CustomToast.SUCCESS,
                            true
                        ).show()
                    }
                } else CustomToast.makeText(
                    mContext,
                    "Content not available",
                    CustomToast.LENGTH_SHORT,
                    CustomToast.ERROR,
                    true
                ).show()
            } else {
                if (txtInt == item?.txtIn) {
                    val model = getModel(txtInt, txtOut)
                    val isFavorite: Boolean
                    if (!App.getDb().appDao().isExistTranslate(txtOut, txtInt)) {
                        model.isFavorite = item?.isFavorite != true
                        item?.isFavorite = model.isFavorite
                        isFavorite = model.isFavorite
                        App.getDb().appDao().insertTranslate(model)
                    } else {
                        isFavorite = item?.isFavorite != true
                        item?.isFavorite = isFavorite
                        App.getDb().appDao().updateFavorite(isFavorite, txtOut, txtInt)
                    }
                    if (isFavorite) {
                        binding.iconHeart.setImageResource(R.drawable.ic_favorite_text)
                        CustomToast.makeText(
                            mContext,
                            "Favorite success",
                            CustomToast.LENGTH_SHORT,
                            CustomToast.SUCCESS,
                            true
                        ).show()
                    } else {
                        binding.iconHeart.setImageResource(R.drawable.icon_heart_unselect_text)
                        CustomToast.makeText(
                            mContext,
                            "Un favorite success",
                            CustomToast.LENGTH_SHORT,
                            CustomToast.SUCCESS,
                            true
                        ).show()
                    }
                } else {
                    if (txtOut.isNotEmpty()) {
                        val model = getModel(txtInt, txtOut)
                        if (!App.getDb().appDao().isExistTranslate(txtOut, txtInt)) {
                            model.isFavorite = true
                            App.getDb().appDao().insertTranslate(model)
                        } else App.getDb().appDao().updateFavorite(true, txtOut, txtInt)
                        binding.iconHeart.setImageResource(R.drawable.ic_favorite_text)
                        CustomToast.makeText(
                            mContext,
                            "Favorite success",
                            CustomToast.LENGTH_SHORT,
                            CustomToast.SUCCESS,
                            true
                        ).show()
                    } else CustomToast.makeText(
                        mContext,
                        "Content not available",
                        CustomToast.LENGTH_SHORT,
                        CustomToast.ERROR,
                        true
                    ).show()
                }
            }
        }
        return binding.root
    }

    private var isCheck = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.edtInputText.addTextChangedListener {
            if (item != null) {
                if (it.toString() != item?.txtIn) {
                    binding.iconHeart.setImageResource(R.drawable.icon_heart_unselect_text)
                } else {
                    if (item?.isFavorite == true) {
                        binding.iconHeart.setImageResource(R.drawable.ic_favorite_text)
                    }
                }
            }
        }
    }

    private fun setFromLanguage(item: ListLanguage) {
        itemInLanguage = item
        txtLanguageInput.text = item.txtNameLanguage
        flagInputTextTranslate.setImageResource(item.imgFlag)
        flagInputTextTranslate.tag = item.imgFlag
        imgFlagTxtIn.setImageResource(item.imgFlag)
        imgFlagTxtIn.tag = item.imgFlag
        App.dataPer.putString(Params.KEY_LANGUAGE_FROM_TEXT, item.txtShortNameLanguage)
    }

    private fun setToLanguage(item: ListLanguage) {
        itemOuLanguage = item
        txtLanguageOutput.text = item.txtNameLanguage
        flagOutputTextTranslate.setImageResource(item.imgFlag)
        flagOutputTextTranslate.tag = item.imgFlag
        imgFlagTxtOut.setImageResource(item.imgFlag)
        imgFlagTxtOut.tag = item.imgFlag
        App.dataPer.putString(Params.KEY_LANGUAGE_TO_TEXT, item.txtShortNameLanguage)
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        val fromText = App.dataPer.getString(Params.KEY_LANGUAGE_FROM_TEXT, "").toString()
        val toText = App.dataPer.getString(Params.KEY_LANGUAGE_TO_TEXT, "").toString()
        if (item == null) {
            arrayList.forEach {
                if (fromText.isEmpty()) {
                    if (it.txtShortNameLanguage == Locale.getDefault().language) {
                        setFromLanguage(it)
                    }
                } else {
                    if (fromText == it.txtShortNameLanguage) {
                        setFromLanguage(it)
                    }
                }

                if (toText.isEmpty()) {
                    if (Locale.getDefault().language != "en") {
                        if (it.txtShortNameLanguage == TranslateLanguage.ENGLISH) {
                            setToLanguage(it)
                        }
                    } else if (it.txtShortNameLanguage != Locale.getDefault().language) {
                        setToLanguage(it)
                    }
                } else {
                    if (toText == it.txtShortNameLanguage) {
                        setToLanguage(it)
                    }
                }
            }
        } else {
            binding.edtInputText.text = item?.txtIn.toString().toEditable()
            txtDisplay.text = item?.txtOut.toString()
            if (item?.isFavorite == true) {
                binding.iconHeart.setImageResource(R.drawable.ic_favorite_text)
            }
            arrayList.forEach {
                if (item?.toLanguage == it.txtShortNameLanguage) {
                    setToLanguage(it)
                }

                if (item?.fromLanguage == it.txtShortNameLanguage) {
                    setFromLanguage(it)
                }
            }
        }
    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
    private fun setUpProgressDialog() {
        pDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "Loading"
        pDialog.setCancelable(false)
    }

    private fun setFilter(text: String) {
        val filterList: ArrayList<ListLanguage> = ArrayList()
        for (item: ListLanguage in arrayList) {
            if (item.txtNameLanguage.lowercase().contains(text.lowercase())) {
                filterList.add(item)
            }
        }
        spinnerAdapter.filterList(filterList)
    }

    private fun prepareTranslateModel() {
//        if ((itemInLanguage == null && itemOuLanguage == null) || itemOuLanguage == null || itemInLanguage == null) {
        TranslationUtils.getTranslatorOptions(
            originalString,
            itemInLanguage!!.txtShortNameLanguage,
            itemOuLanguage!!.txtShortNameLanguage,
            callBack = object : TranslationUtils.OnTranslationCompleteListener {
                override fun onStartTranslation() {
                    pDialog.titleText = "Translate Model Downloading ...."
                    pDialog.show()
                }

                override fun onCompleted(text: String?) {
                    pDialog.dismiss()
                    binding.iconHeart.setImageResource(R.drawable.icon_heart_unselect_text)
                    item = null
                    txtDisplay.text = text.toString()
                    val txtInt = binding.edtInputText.text.toString().trim()
                    val txtOut = binding.txtDisplayOutputText.text.toString().trim()
                    val model = getModel(txtInt, txtOut)
                    if (!App.getDb().appDao().isExistTranslate(txtOut, txtInt)) {
                        App.getDb().appDao().insertTranslate(model)
                    }
                }

                override fun onError(e: Exception?) {
                    pDialog.dismiss()
                    Toast.makeText(view?.context, "Error ${e?.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        )
    }

    private fun getModel(txtInt: String, txtOut: String): TranslateHistory {
        return TranslateHistory(
            0, imgFlagTxtIn.tag as Int, txtInt,
            imgFlagTxtOut.tag as Int, txtOut,
            App.dataPer.getString(Params.KEY_LANGUAGE_TO_TEXT, "").toString(),
            App.dataPer.getString(Params.KEY_LANGUAGE_FROM_TEXT, "").toString()
        )
    }

    private fun identifyLanguageWithStringInput(text: String) {
        val languageIdentifier = LanguageIdentification.getClient()
        languageIdentifier.identifyLanguage(text)
            .addOnSuccessListener { languageCode ->
                if (languageCode != "und") {
                    LogInstance.e(languageCode)
                    arrayList.forEach {
                        if (it.txtShortNameLanguage == languageCode) {
                            setFromLanguage(it)
                        }
                    }
                    prepareTranslateModel()
                } else {
                    LogInstance.e("not found")
                    prepareTranslateModel()
                }
            }
            .addOnFailureListener {
                prepareTranslateModel()
                LogInstance.e("exception processing $it")
            }
    }

    private var item: TranslateHistory? = null
    override fun setArguments(args: Bundle?) {
        super.setArguments(args)
        item = args?.getSerializable("item") as TranslateHistory
    }

    override fun onDestroyView() {
        onStopSpeech()
        item = null
        super.onDestroyView()
    }
}