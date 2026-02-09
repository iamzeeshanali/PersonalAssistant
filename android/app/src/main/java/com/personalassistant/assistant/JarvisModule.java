package com.personalassistant.assistant;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class JarvisModule extends ReactContextBaseJavaModule {

    public JarvisModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "JarvisModule";
    }

    @ReactMethod
    public void startJarvisOverlay() {
        Context context = getReactApplicationContext();

        Log.d("Jarvis", "startJarvisOverlay called from RN");

        // 1️⃣ Overlay permission check
        if (!Settings.canDrawOverlays(context)) {
            Intent permissionIntent = new Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + context.getPackageName())
            );
            permissionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(permissionIntent);
            return;
        }

        // 2️⃣ Start service (ALL ANDROID VERSIONS)
        Intent serviceIntent = new Intent(context, JarvisService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }
}
