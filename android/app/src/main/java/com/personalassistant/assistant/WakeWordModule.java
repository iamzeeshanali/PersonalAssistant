package com.personalassistant.assistant;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.concurrent.atomic.AtomicBoolean;

public class WakeWordModule extends ReactContextBaseJavaModule {

    private static final String TAG = "WakeWordModule";
    private static final String EVENT_WAKEWORD_DETECTED = "onWakeWordDetected";

    private final ReactApplicationContext reactContext;
    private AudioRecord audioRecord;
    private Thread listeningThread;
    private final AtomicBoolean isListening = new AtomicBoolean(false);

    // Audio config - match your model's expected input (most common for wake word models)
    private static final int SAMPLE_RATE = 16000;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(
            SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT) * 2;  // larger buffer helps

    // Placeholder sensitivity / threshold (replace with real model confidence)
    private static final float ENERGY_THRESHOLD = 1200.0f;  // dummy value

    public WakeWordModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "WakeWord";
    }

    @ReactMethod
    public void startListening(Promise promise) {
        if (isListening.get()) {
            promise.resolve(true);
            return;
        }

        if (ContextCompat.checkSelfPermission(reactContext, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            promise.reject("PERMISSION_DENIED", "RECORD_AUDIO permission not granted");
            return;
        }

        try {
            audioRecord = new AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    SAMPLE_RATE,
                    CHANNEL_CONFIG,
                    AUDIO_FORMAT,
                    BUFFER_SIZE
            );

            if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
                promise.reject("AUDIO_INIT_FAILED", "AudioRecord initialization failed");
                return;
            }

            isListening.set(true);
            listeningThread = new Thread(this::listeningLoop);
            listeningThread.start();

            promise.resolve(true);
            Log.i(TAG, "Wake word listening started");
        } catch (Exception e) {
            isListening.set(false);
            promise.reject("START_FAILED", e.getMessage());
        }
    }

    @ReactMethod
    public void stopListening(Promise promise) {
        isListening.set(false);
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
        if (listeningThread != null && listeningThread.isAlive()) {
            try {
                listeningThread.join(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        promise.resolve(true);
        Log.i(TAG, "Wake word listening stopped");
    }

    private void listeningLoop() {
        short[] buffer = new short[BUFFER_SIZE / 2];  // short = 16-bit PCM

        audioRecord.startRecording();

        while (isListening.get()) {
            int read = audioRecord.read(buffer, 0, buffer.length);
            if (read <= 0) {
                continue;
            }

            // -------------------------------
            //  Real implementation:
            //  1. Preprocess buffer → features (MFCC, mel-spectrogram, etc.)
            //  2. Feed features to ONNX / TFLite model
            //  3. Get confidence score
            // -------------------------------

            // Dummy energy-based detection (REPLACE THIS!)
            float energy = calculateEnergy(buffer, read);
            if (energy > ENERGY_THRESHOLD) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    emitWakeWordDetected("Hey Jarvis");
                });
                // Optional: add cooldown / debounce (e.g. sleep 1500ms)
            }
        }

        audioRecord.stop();
        audioRecord.release();
        audioRecord = null;
    }

    // Dummy RMS energy calculation – replace with real preprocessing + inference
    private float calculateEnergy(short[] buffer, int length) {
        long sum = 0;
        for (int i = 0; i < length; i++) {
            sum += (long) buffer[i] * buffer[i];
        }
        return (float) Math.sqrt(sum / (float) length);
    }

    private void emitWakeWordDetected(String keyword) {
        if (!reactContext.hasActiveCatalystInstance()) {
            return;
        }

        WritableMap params = Arguments.createMap();
        params.putString("keyword", keyword);
        params.putDouble("timestamp", System.currentTimeMillis());

        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(EVENT_WAKEWORD_DETECTED, params);
    }

    @Override
    public void invalidate() {
        stopListening(null);  // force cleanup
        super.invalidate();
    }
}
