package com.personalassistant

import android.app.Application
import com.facebook.react.PackageList
import com.facebook.react.ReactApplication
import com.facebook.react.ReactHost
import com.facebook.react.ReactNativeApplicationEntryPoint.loadReactNative
import com.facebook.react.defaults.DefaultReactHost.getDefaultReactHost
import com.personalassistant.assistant.VoicePackage
import com.personalassistant.assistant.OpenAppPackage
import com.personalassistant.assistant.CallPackage
import com.personalassistant.assistant.WakeWordPackage
import com.personalassistant.assistant.JarvisPackage

public class MainApplication : Application(), ReactApplication {

    override val reactHost: ReactHost by lazy {
        getDefaultReactHost(
            context = applicationContext,
            packageList =
                PackageList(this).packages.apply {
                    // Packages that cannot be autolinked yet can be added manually here, for example:
                    add(VoicePackage())
                    add(OpenAppPackage())
                    add(CallPackage())
                    add(WakeWordPackage())
                    add(JarvisPackage())
                },
        )
    }

    override fun onCreate() {
        super.onCreate()
        loadReactNative(this)
    }
}
