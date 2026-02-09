package com.personalassistant.assistant;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.personalassistant.R;

public class JarvisNativeOverlay {

    private final Context context;
    private final WindowManager windowManager;
    private View overlayView;

    public JarvisNativeOverlay(Context context) {
        this.context = context;
        this.windowManager =
                (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    public void show() {
        if (overlayView != null) return;

        LayoutInflater inflater = LayoutInflater.from(context);
        overlayView = inflater.inflate(R.layout.jarvis_overlay, null);

        ImageView btnClose = overlayView.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> hide());

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.BOTTOM;
        windowManager.addView(overlayView, params);
//        new Handler(Looper.getMainLooper()).postDelayed(this::hide, 5000);
    }

    public void hide() {
        if (overlayView != null) {
            windowManager.removeView(overlayView);
            overlayView = null;
        }
    }
}
