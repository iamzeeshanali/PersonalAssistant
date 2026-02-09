package com.personalassistant.assistant;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
import android.provider.Settings;


import androidx.core.app.NotificationCompat;
import com.personalassistant.R;

public class JarvisService extends Service {

    private JarvisNativeOverlay overlay;
    private AudioManager audioManager;
    private int lastVolume;
    private ContentObserver volumeObserver;
    private long lastTriggerTime = 0;


    @Override
    public void onCreate() {
        super.onCreate();
        overlay = new JarvisNativeOverlay(this);

        Log.d("Jarvis", "JarvisService created");

        // 🔊 Volume detection setup
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        lastVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        volumeObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);

                int currentVolume =
                        audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

                if (currentVolume < lastVolume) {
                    Log.d("Jarvis", "Volume DOWN detected");

                    showJarvisOverlaySafely();
                }

                lastVolume = currentVolume;
            }
        };

        getContentResolver().registerContentObserver(
                Settings.System.CONTENT_URI,
                true,
                volumeObserver
        );
    }

    private void showJarvisOverlaySafely() {
        if (!Settings.canDrawOverlays(this)) return;

        long now = System.currentTimeMillis();
        if (now - lastTriggerTime < 800) return; // debounce
        lastTriggerTime = now;

        overlay.show();
        JarvisVoiceEngine
                .getInstance(this)
                .startListening();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForegroundService();
        Log.d("Jarvis", "JarvisService started");
        return START_STICKY;
    }

    private void startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "jarvis_channel",
                    "Jarvis Assistant",
                    NotificationManager.IMPORTANCE_LOW
            );

            NotificationManager manager =
                    getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

            Notification notification =
                    new NotificationCompat.Builder(this, "jarvis_channel")
                            .setContentTitle("Jarvis running")
                            .setContentText("Listening for commands")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .build();

            startForeground(1, notification);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (volumeObserver != null) {
            getContentResolver().unregisterContentObserver(volumeObserver);
        }
    }
}
