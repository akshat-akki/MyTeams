package com.example.engageteams;

import android.app.Application;
import android.content.Intent;

import com.example.engageteams.UI.Auth.AuthenticationActivity;
import com.example.engageteams.UI.SplashActivity;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import io.reactivex.android.schedulers.AndroidSchedulers;
import sdk.chat.app.firebase.ChatSDKFirebase;
import sdk.chat.core.module.Module;
import sdk.chat.core.session.ChatSDK;
import sdk.chat.core.types.AccountDetails;
import sdk.chat.firebase.adapter.module.FirebaseModule;
import sdk.chat.firebase.push.FirebasePushModule;
import sdk.chat.firebase.ui.FirebaseUIModule;
import sdk.chat.firebase.upload.FirebaseUploadModule;
import sdk.chat.ui.module.UIModule;
import sdk.guru.common.RX;

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
