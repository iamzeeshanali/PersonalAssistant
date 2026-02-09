package com.personalassistant.assistant;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class CallModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext context;

    public CallModule(ReactApplicationContext reactContext) {
        super(reactContext);
        context = reactContext;
    }

    @Override
    public String getName() {
        return "CallModule";
    }

    @ReactMethod
    public void call(String contactName) {

        // 1️⃣ Permissions
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    getCurrentActivity(),
                    new String[]{
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.CALL_PHONE
                    },
                    201
            );
            return;
        }

        System.out.println("Before Query");

        // 2️⃣ Query contacts
        Cursor cursor = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ?",
                new String[]{contactName},
                null
        );

        System.out.println("Before If");
        System.out.println(cursor);
        System.out.println("cursor");
        if (cursor != null && cursor.moveToFirst()) {
            System.out.println("Inside If");
            @SuppressLint("Range") String phoneNumber = cursor.getString(
                    cursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                    )
            );
            cursor.close();

            System.out.println("Before Calling Intent");
            // 3️⃣ Call
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phoneNumber.replaceAll("\\s+", "")));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else if (cursor != null) {
            cursor.close();
        }
    }
}
