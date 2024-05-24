package com.angelhr28.voicegpt

import android.annotation.SuppressLint
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast

class FloatingButtonService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var floatingButton: View
    private lateinit var speechRecognizer: SpeechRecognizer

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate() {
        super.onCreate()
        floatingButton = LayoutInflater.from(this).inflate(R.layout.layout_floating_button, null)
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        windowManager.addView(floatingButton, params)

//        buildSpeechRecognizer()

        binding(params)

        val filter = IntentFilter("SPEECH_RECOGNITION_RESULT")
        registerReceiver(speechResultReceiver, filter)

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun binding(params: WindowManager.LayoutParams) {
        val closeButton = floatingButton.findViewById<ImageView>(R.id.floating_button)
        closeButton.setOnTouchListener(object : View.OnTouchListener {
            private var initialX: Int = 0
            private var initialY: Int = 0
            private var initialTouchX: Float = 0.toFloat()
            private var initialTouchY: Float = 0.toFloat()

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {

                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        clickButton()
                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager.updateViewLayout(floatingButton, params)
                        return true
                    }
                }
                return false
            }
        })
    }

    private fun clickButton() {
        val intent = Intent("START_SPEECH_RECOGNIZER")
        Log.e("FloatingButtonService", "Enviando transmisión: START_SPEECH_RECOGNIZER")
        sendBroadcast(intent)
        Toast.makeText(this, "Botón flotante clicado", Toast.LENGTH_SHORT).show()


//        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
//        intent.putExtra(
//            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
//        )
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es")
//        speechRecognizer.startListening(intent)


    }


//    private fun buildSpeechRecognizer() {
//        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
//        speechRecognizer.setRecognitionListener(object : RecognitionListener {
//            override fun onReadyForSpeech(params: Bundle?) {}
//
//            override fun onBeginningOfSpeech() {}
//
//            override fun onRmsChanged(rmsdB: Float) {}
//
//            override fun onBufferReceived(buffer: ByteArray?) {}
//
//            override fun onEndOfSpeech() {}
//
//            override fun onError(error: Int) {
//
//                Log.e("FloatingButtonService", "error: $error")
//                Toast.makeText(this@FloatingButtonService, "Error en el reconocimiento de voz", Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onResults(results: Bundle?) {
//                val result =
//                    results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0)
//
//                Log.e("FloatingButtonService", "Resultado: ${results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)}")
//                Log.e("FloatingButtonService", "Resultado: $result")
//
//                Toast.makeText(this@FloatingButtonService, "Resultado: $result", Toast.LENGTH_SHORT).show()
//
//            }
//
//            override fun onPartialResults(partialResults: Bundle?) {}
//
//            override fun onEvent(eventType: Int, params: Bundle?) {}
//        })
//    }


    private val speechResultReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == "SPEECH_RECOGNITION_RESULT") {
                val result = intent.getStringExtra("result")
                // Manejar el resultado aquí
                Toast.makeText(this@FloatingButtonService, "Resultado: $result", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        if (::floatingButton.isInitialized) {
            windowManager.removeView(floatingButton)
        }
        unregisterReceiver(speechResultReceiver)
    }
}