package com.example.engageteams;

import android.app.Application;

import com.example.engageteams.UI.SplashActivity;

import java.util.concurrent.TimeUnit;

import sdk.chat.contact.ContactBookModule;
import sdk.chat.core.session.ChatSDK;
import sdk.chat.encryption.firebase.FirebaseEncryptionModule;
import sdk.chat.firbase.online.FirebaseLastOnlineModule;
import sdk.chat.firebase.adapter.module.FirebaseModule;
import sdk.chat.firebase.blocking.FirebaseBlockingModule;
import sdk.chat.firebase.push.FirebasePushModule;
import sdk.chat.firebase.receipts.FirebaseReadReceiptsModule;
import sdk.chat.firebase.typing.FirebaseTypingIndicatorModule;
import sdk.chat.firebase.upload.FirebaseUploadModule;
import sdk.chat.message.audio.AudioMessageModule;
import sdk.chat.message.file.FileMessageModule;
import sdk.chat.message.sticker.module.StickerMessageModule;
import sdk.chat.message.video.VideoMessageModule;
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
                    //external modules addition
                    .addModule(AudioMessageModule.shared())
                    .addModule(VideoMessageModule.shared())
                    .addModule(StickerMessageModule.shared())
                    .addModule(ContactBookModule.shared())
                    .addModule(FirebaseEncryptionModule.shared())
                    .addModule(FirebaseTypingIndicatorModule.shared())
                    .addModule(FirebaseReadReceiptsModule.shared())
                    .addModule(FirebaseLastOnlineModule.shared())
                    .addModule(FirebaseBlockingModule.shared())
                    .addModule(FileMessageModule.shared())



                    // Add modules to handle file uploads, push notifications
                    .addModule(FirebaseUploadModule.shared())
                    .addModule(FirebasePushModule.shared())
                    // Activate
                    .build()
                    .activate(this,"akshat.akki2000@gmail.com");

        } catch (Exception e) {
            e.printStackTrace();
        }
ChatSDK.ui().setSplashScreenActivity(SplashActivity.class);

    }
}
