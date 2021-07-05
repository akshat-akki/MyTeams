package com.example.engageteams.UI.MeetRooms;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.engageteams.R;
import com.facebook.react.modules.core.PermissionListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import org.jitsi.meet.sdk.BroadcastEvent;
import org.jitsi.meet.sdk.BroadcastIntentHelper;
import org.jitsi.meet.sdk.JitsiMeetActivityDelegate;
import org.jitsi.meet.sdk.JitsiMeetActivityInterface;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetOngoingConferenceService;
import org.jitsi.meet.sdk.JitsiMeetView;
import org.jitsi.meet.sdk.log.JitsiMeetLogger;

import java.net.MalformedURLException;
import java.net.URL;

import timber.log.Timber;
//public class MeetNow extends JitsiMeetActivity {

 public  class MeetNow extends FragmentActivity implements JitsiMeetActivityInterface {
     private JitsiMeetView view;
     String room_name;
     JitsiMeetOngoingConferenceService service = new JitsiMeetOngoingConferenceService();
     FirebaseAuth auth = FirebaseAuth.getInstance();
     FirebaseUser currentUser = auth.getCurrentUser();
     private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
         @Override
         public void onReceive(Context context, Intent intent) {
             onBroadcastReceived(intent);
         }
     };

     @Override
     public void onBackPressed() {
         // super.onBackPressed();

         view.enterPictureInPicture();
//         JitsiMeetActivityDelegate.onBackPressed();
//         Intent i=new Intent(getApplicationContext(), MainActivity.class);
//         i.putExtra("Profile_pic_URL",currentUser.getPhotoUrl());
//         onNewIntent(i);

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
         Intent i = getIntent();
         room_name = i.getStringExtra("Meet_ID");

         view = new JitsiMeetView(MeetNow.this);
         FrameLayout videoView = (FrameLayout) findViewById(R.id.videoview);
         JitsiMeetConferenceOptions options = null;
         try {
             options = new JitsiMeetConferenceOptions.Builder()
                     .setServerURL(new URL("https://meet.jit.si"))
                     .setRoom(room_name)
                     .setVideoMuted(true)
                     .setAudioMuted(true)
                     .setFeatureFlag("invite.enabled", false)
                     .setFeatureFlag("pip.enabled", true)
                     .setFeatureFlag("lobby-mode.enabled", false)
                     .build();
         } catch (MalformedURLException e) {
             e.printStackTrace();
         }
         view.join(options);
         videoView.addView(view);
         registerForBroadcastMessages();
     }

     @Override
     protected void onDestroy() {
         leave();
         super.onDestroy();

     }

     @Override
     public void onNewIntent(Intent intent) {
         super.onNewIntent(intent);

         JitsiMeetActivityDelegate.onNewIntent(intent);
     }

     @SuppressLint("MissingSuperCall")
     @Override
     public void onRequestPermissionsResult(
             final int requestCode,
             final String[] permissions,
             final int[] grantResults) {
         JitsiMeetActivityDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults);
     }

//     public void onInviteClick(View v) {
//         createinvitelinks();
//     }


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
                 case CONFERENCE_TERMINATED:
                     finish();

             }
         }
     }

     // Example for sending actions to JitsiMeetSDK
     private void hangUp() {
         Intent hangupBroadcastIntent = BroadcastIntentHelper.buildHangUpIntent();
         LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(hangupBroadcastIntent);
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
     public void leave() {

         if (view != null) {
             view.leave();
         } else {
             JitsiMeetLogger.w("Cannot leave, view is null");
         }
     }
     @Override
     public void finish() {
         leave();
         super.finish();
     }


 }


