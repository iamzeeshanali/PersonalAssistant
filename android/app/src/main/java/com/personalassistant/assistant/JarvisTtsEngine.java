package com.personalassistant.assistant;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class JarvisTtsEngine {

    private static JarvisTtsEngine instance;
    private TextToSpeech tts;
    private boolean isReady = false;

    private JarvisTtsEngine(Context context) {
        tts = new TextToSpeech(context.getApplicationContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
                tts.setSpeechRate(1.0f);
                tts.setPitch(0.5f);
                isReady = true;
                Log.d("JarvisTTS", "TTS initialized");
            }
        });
    }

    public static synchronized JarvisTtsEngine getInstance(Context context) {
        if (instance == null) {
            instance = new JarvisTtsEngine(context);
        }
        return instance;
    }

    public void speak(String text) {
        if (!isReady) {
            Log.w("JarvisTTS", "TTS not ready yet");
            return;
        }

        tts.speak(
                text,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "JARVIS_TTS"
        );
    }

    public void stop() {
        if (tts != null) tts.stop();
    }

    public void destroy() {
        if (tts != null) {
            tts.shutdown();
            tts = null;
        }
        instance = null;
    }
}
