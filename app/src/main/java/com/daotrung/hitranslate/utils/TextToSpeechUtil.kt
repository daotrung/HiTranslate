package com.daotrung.hitranslate.utils

import android.app.Activity
import android.speech.tts.TextToSpeech
import android.widget.Toast
import java.util.*

object TextToSpeechUtil {
    var textToSpeech: TextToSpeech? = null
    fun Activity.covertTextToSpeech(text: String, language: String) {
        onStopSpeech()
        if (text == "" || text.isEmpty()) {
            Toast.makeText(this, "Content not available", Toast.LENGTH_SHORT).show()
        } else {
            textToSpeech = TextToSpeech(this,
                { p0 ->
                    if (p0 == TextToSpeech.SUCCESS) {
                        if (!textToSpeech?.isSpeaking!!) {
                            val result = if (language.isNullOrEmpty()) {
                                textToSpeech?.isLanguageAvailable(Locale.getDefault())
                            } else textToSpeech?.isLanguageAvailable(Locale.forLanguageTag(language))

                            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                                Toast.makeText(
                                    this,
                                    "This Language is not supported",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                if (language.isEmpty()) {
                                    textToSpeech?.language = Locale.getDefault()
                                } else {
                                    textToSpeech?.language = Locale.forLanguageTag(language)
                                }
                                textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null)
                            }
                        } else {
                            Toast.makeText(
                                this,
                                "Please again in a seconds",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else Toast.makeText(
                        this,
                        "This Language is not supported",
                        Toast.LENGTH_SHORT
                    ).show()
                }, "com.google.android.tts"
            )

        }
    }

    fun onStopSpeech() {
        if (textToSpeech != null) {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        }
    }
}