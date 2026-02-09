package com.personalassistant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint;
import com.facebook.react.defaults.DefaultReactActivityDelegate;
import com.personalassistant.assistant.JarvisActions;

public class MainActivity extends ReactActivity {

    private boolean isReceiverRegistered = false;

    /**
     * Returns the name of the main component registered from JavaScript.
     * This is used to schedule rendering of the component.
     */
    @Override
    protected String getMainComponentName() {
        return "PersonalAssistant";
    }

    private BroadcastReceiver jarvisReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("JARVIS_ACTION_BRIGHTNESS_LOW".equals(intent.getAction())) {
                JarvisActions.lowerBrightness(MainActivity.this);
            }
            if ("JARVIS_ACTION_BRIGHTNESS_HIGH".equals(intent.getAction())) {
                JarvisActions.higherBrightness(MainActivity.this);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (!isReceiverRegistered) {
            IntentFilter filter = new IntentFilter("JARVIS_ACTION_BRIGHTNESS_LOW");
            IntentFilter higherBrightnessFilter = new IntentFilter("JARVIS_ACTION_BRIGHTNESS_HIGH");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(jarvisReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
                registerReceiver(jarvisReceiver, higherBrightnessFilter, Context.RECEIVER_NOT_EXPORTED);
            } else {
                registerReceiver(jarvisReceiver, filter);
                registerReceiver(jarvisReceiver, higherBrightnessFilter);
            }
            isReceiverRegistered = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isReceiverRegistered) {
            try {
                unregisterReceiver(jarvisReceiver);
                isReceiverRegistered = false;
            } catch (IllegalArgumentException e) {
                // Receiver was not registered
            }
        }
    }

    /**
     * Returns the instance of the ReactActivityDelegate. We use DefaultReactActivityDelegate
     * which allows you to enable New Architecture with a single boolean flag fabricEnabled
     */
    @Override
    protected ReactActivityDelegate createReactActivityDelegate() {
        return new DefaultReactActivityDelegate(
            this,
            getMainComponentName(),
            DefaultNewArchitectureEntryPoint.getFabricEnabled()
        );
    }
}