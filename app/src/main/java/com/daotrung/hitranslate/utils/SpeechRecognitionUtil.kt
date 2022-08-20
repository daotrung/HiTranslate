package com.daotrung.hitranslate.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognitionService
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.*

object SpeechRecognitionUtil {
    const val logTag = "SpeechRecognitionUtil"

    fun getRecognizerIntent(local: String): Intent {
        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            if (local.isNullOrEmpty()) {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            } else putExtra(RecognizerIntent.EXTRA_LANGUAGE, local)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 20)
            putExtra(
                RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS,
                (5000).toLong()
            )
            putExtra(
                RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,
                (5000).toLong()
            )
            putExtra(
                RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,
                (5000).toLong()
            )
        }
        return speechRecognizerIntent
    }

    fun setupSpeechRecognizer(
        context: Context,
        onError: (Int) -> Unit = {},
        onReadyForSpeech: (Bundle) -> Unit = {},
        onEndOfSpeech: () -> Unit = {},
        onResults: (Bundle) -> Unit = {},
        onEvent: (Bundle) -> Unit = {},
        onPartialResults: (Bundle) -> Unit = {},
        onBufferReceived_: (ByteArray) -> Unit = {},
        onEvent_: (Int, Bundle) -> Unit = { _, _ -> }
    ): SpeechRecognizer? {
        return when {
            SpeechRecognizer.isRecognitionAvailable(context) -> {
                val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
                speechRecognizer.setRecognitionListener(SimpleRecognitionListener(
                    onError_ = { code: Int ->
                        onError(code)
                    },
                    onBufferReceived_ = {
                        onBufferReceived_(it)
                    },
                    onReadyForSpeech_ = {
                        onReadyForSpeech(it)
                    },
                    onBeginningOfSpeech_ = {
                    },
                    onEvent_ = { integer: Int, bundle: Bundle ->
                        onEvent_(integer, bundle)
                    },
                    onEndOfSpeech_ = {
                        onEndOfSpeech()
                    },
                    onPartialResults_ = {
                        onPartialResults(it)
                    },
                    onResults_ = {
                        onResults(it)
                    }
                ))
                speechRecognizer
            }
            else -> {
                Log.i("SpeechRecognizer", "Recognition is NOT available!")
                null
            }
        }
    }
}

fun Bundle.recognizedWords() = getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
fun Bundle.confidenceScores() = getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES)

class SimpleRecognitionListener(
    val onReadyForSpeech_: (Bundle) -> Unit = {},
    val onBeginningOfSpeech_: () -> Unit = {},
    val onRmsChanged_: (Float) -> Unit = {},
    val onBufferReceived_: (ByteArray) -> Unit = {},
    val onEndOfSpeech_: () -> Unit = {},
    val onError_: (Int) -> Unit = {},
    val onResults_: (Bundle) -> Unit = {},
    val onPartialResults_: (Bundle) -> Unit = {},
    val onEvent_: (Int, Bundle) -> Unit = { _, _ -> }
) : RecognitionListener {
    private var performingSpeechSetup = true

    override fun onReadyForSpeech(bundle: Bundle) {
        Log.d(SpeechRecognitionUtil.logTag, "onReadyForSpeech()::Bundle=$bundle")
        performingSpeechSetup = false
        onReadyForSpeech_(bundle)
    }

    override fun onBeginningOfSpeech() {
        Log.d(SpeechRecognitionUtil.logTag, "onBeginningOfSpeech()")
        onBeginningOfSpeech_()
    }

    override fun onRmsChanged(rms: Float) {
//        Log.i(logTag, "onRmsChanged()::Rms=$rms")
        onRmsChanged_(rms)
    }

    override fun onBufferReceived(byteArray: ByteArray) {
        Log.d(
            SpeechRecognitionUtil.logTag,
            "onBufferReceived()::byteArray=$byteArray, \nwords="
        )
        onBufferReceived_(byteArray)
    }

    override fun onEndOfSpeech() {
        Log.d(SpeechRecognitionUtil.logTag, "onEndOfSpeech()")
        onEndOfSpeech_()
    }

    override fun onError(code: Int) {
        Log.d(SpeechRecognitionUtil.logTag, "onError()::code=$code")
        if (performingSpeechSetup && code == SpeechRecognizer.ERROR_NO_MATCH) return
        else {
            onError_(code)
        }
    }

    override fun onResults(bundle: Bundle) {
        Log.d(
            SpeechRecognitionUtil.logTag,
            "onResults()::Bundle=$bundle, \n" +
                    "Recognized words=${bundle.recognizedWords()}\n" +
                    "Confidence scores=${bundle.confidenceScores()}"
        )
        onResults_(bundle)
    }

    override fun onPartialResults(bundle: Bundle) {
        Log.d(
            SpeechRecognitionUtil.logTag, "onPartialResults()::Bundle=$bundle, \n" +
                    "Recognized words=${bundle.recognizedWords()}"
        )
        onPartialResults_(bundle)
    }

    override fun onEvent(event: Int, bundle: Bundle) {
        Log.d(SpeechRecognitionUtil.logTag, "onEvent()::event=$event, \nbundle=$bundle")
        onEvent_(event, bundle)
    }
}