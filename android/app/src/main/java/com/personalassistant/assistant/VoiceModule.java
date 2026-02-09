package com.personalassistant.assistant;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;

public class VoiceModule extends ReactContextBaseJavaModule
        implements RecognitionListener {

    private SpeechRecognizer speechRecognizer;
    private final ReactApplicationContext reactContext;

    public VoiceModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @NonNull
    @Override
    public String getName() {
        return "VoiceModule";
    }

    @ReactMethod
    public void startListening() {
        Handler mainHandler = new Handler(Looper.getMainLooper());

        mainHandler.post(() -> {

            if (!SpeechRecognizer.isRecognitionAvailable(reactContext)) {
                return;
            }

            if (speechRecognizer == null) {
                speechRecognizer =
                        SpeechRecognizer.createSpeechRecognizer(reactContext);
                speechRecognizer.setRecognitionListener(this);
            }

            Intent intent =
                    new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

            intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            );
            intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE,
                    "en-US"
            );
            intent.putExtra(
                    RecognizerIntent.EXTRA_PARTIAL_RESULTS,
                    true
            );


            speechRecognizer.startListening(intent);
        });
    }

    @ReactMethod
    public void stopListening() {

        Handler mainHandler = new Handler(Looper.getMainLooper());

        mainHandler.post(() -> {
            if (speechRecognizer != null) {
                speechRecognizer.stopListening();
                speechRecognizer.cancel();
                speechRecognizer.destroy();
                speechRecognizer = null;
            }
        });
    }


    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches =
                results.getStringArrayList(
                        SpeechRecognizer.RESULTS_RECOGNITION
                );

        if (matches != null && !matches.isEmpty()) {
            String text = matches.get(0);

            WritableMap params = Arguments.createMap();
            params.putString("text", text);

            reactContext
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit("onSpeechResult", params);
        }
    }

    @Override
    public void onError(int error) {
        Log.e("VoiceModule", "Speech error code: " + error);
    }
    @Override public void onReadyForSpeech(Bundle params) {}
    @Override public void onBeginningOfSpeech() {}
    @Override public void onRmsChanged(float rmsdB) {}
    @Override public void onBufferReceived(byte[] buffer) {}
    @Override public void onEndOfSpeech() {}
    @Override
    public void onPartialResults(Bundle partialResults) {
        ArrayList<String> matches =
                partialResults.getStringArrayList(
                        SpeechRecognizer.RESULTS_RECOGNITION
                );

        if (matches != null && !matches.isEmpty()) {
            WritableMap params = Arguments.createMap();
            params.putString("text", matches.get(0));

            reactContext
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit("onSpeechPartialResult", params);
        }
    }

    @Override public void onEvent(int eventType, Bundle params) {}
}
