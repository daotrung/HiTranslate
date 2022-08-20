package com.daotrung.hitranslate.utils

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.daotrung.hitranslate.App
import com.daotrung.hitranslate.R
import com.daotrung.hitranslate.model.TranslateHistory
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import okhttp3.*
import okio.IOException
import org.json.JSONArray
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

object TranslationUtils {
    @Throws(IOException::class)
    fun onTranslation(
        fromLanguage: String,
        toLanguage: String,
        text: String,
        callBack: OnTranslationCompleteListener
    ) {
        callBack.onStartTranslation()
        val client = OkHttpClient.Builder().readTimeout(30000L, TimeUnit.MILLISECONDS).build()
        val url =
            "https://translate.googleapis.com/translate_a/single?client=gtx&" +
                    "sl=$fromLanguage&tl=$toLanguage&dt=t&q=${text.trim()}"
        val request = Request.Builder().url(url).build()
        val handler = Handler(Looper.getMainLooper())
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                LogInstance.e(e.message.toString())
                handler.post {
                    callBack.onError(e)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response != null) {
                    try {
                        val body = response.body
                        val byteArrayOutputStream = ByteArrayOutputStream()
                        body?.byteString()?.write(byteArrayOutputStream)
                        val string = byteArrayOutputStream.toString()
                        byteArrayOutputStream.close()
                        try {
                            val jSONArray: JSONArray = JSONArray(string).getJSONArray(0)
                            var str2: String = ""
                            for (i in 0 until jSONArray.length()) {
                                val jSONArray2 = jSONArray.getJSONArray(i)
                                val sb2 = StringBuilder()
                                sb2.append(str2)
                                sb2.append(jSONArray2[0].toString())
                                str2 = sb2.toString()
                            }
                            LogInstance.e(str2)
                            if (str2.isEmpty()) {
                                handler.post {
                                    callBack.onError(null)
                                }
                            } else {
                                handler.post {
                                    callBack.onCompleted(str2)
                                }
                            }
                        } catch (e: Exception) {
                            handler.post {
                                callBack.onError(null)
                            }
                        }
                    } catch (e: IOException) {
                        handler.post {
                            callBack.onError(null)
                        }
                    } catch (e: TimeoutException) {
                        handler.post {
                            callBack.onError(null)
                        }
                    }
                } else {
                    handler.post {
                        callBack.onError(null)
                    }
                }
            }
        })
    }

    interface OnTranslationCompleteListener {
        fun onStartTranslation()
        fun onCompleted(text: String?)
        fun onError(e: Exception?)
    }


    fun getTranslatorOptions(
        text: String,
        fromLanguage: String,
        toLanguage: String,
        callBack: OnTranslationCompleteListener
    ) {

        val options: TranslatorOptions = TranslatorOptions.Builder()
            .setSourceLanguage(fromLanguage)
            .setTargetLanguage(toLanguage)
            .build()
        val textTranslate = Translation.getClient(options)
        callBack.onStartTranslation()
        val handler = Handler(Looper.getMainLooper())
        textTranslate.downloadModelIfNeeded().addOnSuccessListener {
            handler.post {
                textTranslate.translate(text).addOnSuccessListener {
                    LogInstance.e(it)
                    handler.post {
                        callBack.onCompleted(it)
                    }
                }.addOnFailureListener {
                    handler.post {
                        callBack.onError(it)
                    }
                    LogInstance.e(it.message.toString())
                }
            }
        }.addOnFailureListener {
            LogInstance.e(it.message.toString())
        }
    }
}