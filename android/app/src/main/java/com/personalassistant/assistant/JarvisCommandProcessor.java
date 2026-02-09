package com.personalassistant.assistant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class JarvisCommandProcessor {

    public static void process(Context context, String command) {

        command = command.toLowerCase().trim();
        Log.d("JarvisCommand", "Command: " + command);

        if (command.startsWith("call ")) {
            String contactName = command.replace("call", "").trim();
            JarvisActions.callContact(context, contactName);
            return;
        }

        if (command.contains("brightness") && command.contains("lower")) {
            Intent intent = new Intent("JARVIS_ACTION_BRIGHTNESS_LOW");
            intent.setPackage(context.getPackageName()); // Make it explicit
            context.sendBroadcast(intent);
            return;
        }

        if (command.contains("brightness") && command.contains("higher")) {
            Intent intent = new Intent("JARVIS_ACTION_BRIGHTNESS_HIGH");
            intent.setPackage(context.getPackageName()); // Make it explicit
            context.sendBroadcast(intent);
            return;
        }

        // Future commands
        // play music
        // open app
        // send message
        // etc.

        Log.d("JarvisCommand", "Unknown command");
    }
}
