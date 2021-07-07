package com.example.engageteams;

import android.app.Application;

import com.example.engageteams.UI.SplashActivity;

import java.util.concurrent.TimeUnit;

import sdk.chat.core.session.ChatSDK;
import sdk.chat.firebase.adapter.module.FirebaseModule;
import sdk.chat.firebase.push.FirebasePushModule;
import sdk.chat.firebase.upload.FirebaseUploadModule;
import sdk.chat.ui.ChatSDKUI;
import sdk.chat.ui.module.UIModule;

public class MainApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            ChatSDK.builder()
                    .setGoogleMaps("mykey")
                    .setPublicChatRoomLifetimeMinutes(TimeUnit.HOURS.toMinutes(24))
                    .build()

                    // Add the Firebase network adapter module
                    .addModule(
                            FirebaseModule.builder()
                                    .setFirebaseRootPath("pre_1")
                                    .setDevelopmentModeEnabled(true)
                                    .build()
                    )

                    // Add the UI module
                    .addModule(UIModule.builder()
                            .setTheme(R.style.CustomChatSDKTheme)
                            .setPublicRoomCreationEnabled(true)
                            .setPublicRoomsEnabled(true)
                            .setAllowBackPressFromMainActivity(true)
                            .build()

                    )


                    // Add modules to handle file uploads, push notifications
                    .addModule(FirebaseUploadModule.shared())
                    .addModule(FirebasePushModule.shared())
                    // Activate
                    .build()
                    .activate(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
ChatSDK.ui().setSplashScreenActivity(SplashActivity.class);

    }
}
