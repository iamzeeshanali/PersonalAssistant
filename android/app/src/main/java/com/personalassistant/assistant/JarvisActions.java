package com.personalassistant.assistant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class JarvisActions {

    public static void callContact(Context context, String name) {
        System.out.println(name);

        String phoneNumber = getPhoneNumber(context, name);

        if (phoneNumber == null) {
            JarvisTtsEngine
                    .getInstance(context)
                    .speak("I could not find the contact " + name);
            return;
        }

        JarvisTtsEngine
                .getInstance(context)
                .speak("Calling " + name);

        new android.os.Handler(android.os.Looper.getMainLooper())
                .postDelayed(() -> {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + phoneNumber));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    context.startActivity(intent);
                }, 800); // 👈 800ms (adjust to 500 / 1000)


    }

    private static String getPhoneNumber(Context context, String name) {

        Cursor cursor = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE ?",
                new String[]{"%" + name + "%"},
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            String number = cursor.getString(
                    cursor.getColumnIndexOrThrow(
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                    )
            );
            cursor.close();
            return number;
        }

        if (cursor != null) cursor.close();
        return null;
    }

    public static void lowerBrightness(Activity activity) {

        if (activity == null) return;

        Window window = activity.getWindow();

        WindowManager.LayoutParams params = window.getAttributes();
        params.screenBrightness = 0.2f; // 0.0 = dark, 1.0 = bright

        window.setAttributes(params);
    }

    public static void higherBrightness(Activity activity) {

        if (activity == null) return;

        Window window = activity.getWindow();

        WindowManager.LayoutParams params = window.getAttributes();
        params.screenBrightness = 1.0f; // 0.0 = dark, 1.0 = bright

        window.setAttributes(params);
    }
}
