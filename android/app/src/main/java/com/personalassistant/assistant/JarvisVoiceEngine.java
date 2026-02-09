package com.personalassistant.assistant;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;

public class JarvisVoiceEngine {

    private static JarvisVoiceEngine instance;
    private SpeechRecognizer speechRecognizer;
    private Intent recognizerIntent;

    private JarvisVoiceEngine(Context context) {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        );
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        // Unused but required
        RecognitionListener listener = new RecognitionListener() {

            @Override
            public void onReadyForSpeech(Bundle params) {
                Log.d("JarvisVoice", "Ready for speech");
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.d("JarvisVoice", "Speech started");
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches =
                        results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if (matches != null && !matches.isEmpty()) {
                    Log.d("JarvisVoice", "User said: " + matches.get(0));
                    String spokenText = matches.get(0);
                    JarvisCommandProcessor.process(context, spokenText);
                }
            }

            @Override
            public void onError(int error) {
                Log.e("JarvisVoice", "Error: " + error);
            }

            // Unused but required
            @Override
            public void onRmsChanged(float rmsdB) {
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
            }

            @Override
            public void onEndOfSpeech() {
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
            }
        };
        speechRecognizer.setRecognitionListener(listener);
    }

    public static synchronized JarvisVoiceEngine getInstance(Context context) {
        if (instance == null) {
            instance = new JarvisVoiceEngine(context.getApplicationContext());
        }
        return instance;
    }

    public void startListening() {
        Log.d("JarvisVoice", "🎤 Start listening");
        speechRecognizer.startListening(recognizerIntent);
    }

    public void stopListening() {
        Log.d("JarvisVoice", "🛑 Stop listening");
        speechRecognizer.stopListening();
    }

    public void destroy() {
        speechRecognizer.destroy();
        instance = null;
    }

}

