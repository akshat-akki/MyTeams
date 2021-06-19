package com.example.engageteams.UI.MeetRooms;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.engageteams.R;
import com.facebook.react.modules.core.PermissionListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import org.jitsi.meet.sdk.BroadcastEvent;
import org.jitsi.meet.sdk.BroadcastIntentHelper;
import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetActivityDelegate;
import org.jitsi.meet.sdk.JitsiMeetActivityInterface;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetView;

import java.net.MalformedURLException;
import java.net.URL;

import timber.log.Timber;
//public class MeetNow extends JitsiMeetActivity {

 public  class MeetNow extends FragmentActivity implements JitsiMeetActivityInterface {
        private JitsiMeetView view;

        private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onBroadcastReceived(intent);
            }
        };

     @Override
     public void onBackPressed() {
      //   super.onBackPressed();

     }

     @Override
     protected void onActivityResult(
             int requestCode,
             int resultCode,
             Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
         JitsiMeetActivityDelegate.onActivityResult(
                 this, requestCode, resultCode, data);
     }

     @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_meet_now);
            Intent i=getIntent();
            String room_name=i.getStringExtra("Meet_ID");


            view = new JitsiMeetView(MeetNow.this);
           FrameLayout videoView = (FrameLayout) findViewById(R.id.videoview);
            JitsiMeetConferenceOptions options = null;
            try {
                options = new JitsiMeetConferenceOptions.Builder()
                        .setServerURL(new URL("https://meet.jit.si"))
                        .setRoom(room_name)
                        .setFeatureFlag("invite.enabled",false)
                        .setFeatureFlag("lobby-mode.enabled",false)
                        .build();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            view.join(options);

            videoView.addView(view);

            //  Initialize default options for Jitsi Meet conferences.
//            URL serverURL;
//            try {
//                // When using JaaS, replace "https://meet.jit.si" with the proper serverURL
//                serverURL = new URL("https://meet.jit.si");
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//                throw new RuntimeException("Invalid server URL!");
//            }
//            JitsiMeetConferenceOptions defaultOptions
//                    = new JitsiMeetConferenceOptions.Builder()
//                    .setServerURL(serverURL)
//                    .setFeatureFlag("invite.enabled",true)
//                    .setFeatureFlag("" , "need to pass date to jitsi-meet")
//                    .setWelcomePageEnabled(false)
//                    .build();
//            JitsiMeet.setDefaultConferenceOptions(defaultOptions);

            registerForBroadcastMessages();
        }

     @Override
     protected void onDestroy() {
         super.onDestroy();

         view.dispose();
         view = null;

         JitsiMeetActivityDelegate.onHostDestroy(this);
     }
     @Override
     public void onNewIntent(Intent intent) {
         super.onNewIntent(intent);

         JitsiMeetActivityDelegate.onNewIntent(intent);
     }
     @Override
     public void onRequestPermissionsResult(
             final int requestCode,
             final String[] permissions,
             final int[] grantResults) {
         JitsiMeetActivityDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults);
     }
     public void onButtonClick(View v) {
//            EditText editText = findViewById(R.id.editText);
//            String text = editText.getText().toString();
//
//
//            if (text.length() > 0) {
//                //Build options object for joining the conference. The SDK will merge the default
//                //one we set earlier and this one when joining.
//                JitsiMeetConferenceOptions options
//                        = new JitsiMeetConferenceOptions.Builder()
//                        .setRoom(text)
//                        .setFeatureFlag("invite.enabled",true)
//                        .setFeatureFlag("invite-url-custom" , "need to pass date to jitsi-meet")
//                        .setWelcomePageEnabled(false)
//                        .build();

                // Launch the new activity with the given options. The launch() method takes care
                //of creating the required Intent and passing the options.
                //    JitsiMeetActivity.launch(this, options);
            }


        private void registerForBroadcastMessages() {
            IntentFilter intentFilter = new IntentFilter();

            /* This registers for every possible event sent from JitsiMeetSDK
               If only some of the events are needed, the for loop can be replaced
               with individual statements:
               ex:  intentFilter.addAction(BroadcastEvent.Type.AUDIO_MUTED_CHANGED.getAction());
                    intentFilter.addAction(BroadcastEvent.Type.CONFERENCE_TERMINATED.getAction());
                    ... other events
             */
            for (BroadcastEvent.Type type : BroadcastEvent.Type.values()) {
                intentFilter.addAction(type.getAction());
            }

            LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
        }

        // Example for handling different JitsiMeetSDK events
        private void onBroadcastReceived(Intent intent) {
            if (intent != null) {
                BroadcastEvent event = new BroadcastEvent(intent);

                switch (event.getType()) {
                    case CONFERENCE_JOINED:
                        Timber.i("Conference Joined with url%s", event.getData().get("url"));
                        break;
                    case PARTICIPANT_JOINED:
                        Timber.i("Participant joined%s", event.getData().get("name"));
                        break;

                }
            }
        }

        // Example for sending actions to JitsiMeetSDK
        private void hangUp() {
            Intent hangupBroadcastIntent = BroadcastIntentHelper.buildHangUpIntent();
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(hangupBroadcastIntent);
        }




        private void createinvitelinks()
        {
            DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setLink(Uri.parse("https://www.myteams.com/"))
                    .setDomainUriPrefix("https://myteams.page.link")
                  /*  .setAndroidParameters(
                            new DynamicLink.AndroidParameters.Builder("com.example.engageteams")
                                    .setMinimumVersion(125)*/

                    .buildDynamicLink();
            Uri dynamicLinkUri = dynamicLink.getUri();
        }


     @Override
     public void requestPermissions(String[] permissions, int requestCode, PermissionListener listener) {
         JitsiMeetActivityDelegate.requestPermissions(this, permissions, requestCode, listener);
     }
     @Override
     protected void onResume() {
         super.onResume();

         JitsiMeetActivityDelegate.onHostResume(this);
     }

     @Override
     protected void onStop() {
         super.onStop();

         JitsiMeetActivityDelegate.onHostPause(this);
     }


    }


