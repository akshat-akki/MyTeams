package com.example.engageteams.UI.MeetRooms;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.example.engageteams.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetActivityInterface;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetFragment;
import org.jitsi.meet.sdk.JitsiMeetView;


public class MyMeetActivity extends JitsiMeetActivity {
//    public static void launch(Context context, JitsiMeetConferenceOptions options) {
//        Intent intent = new Intent(context, MyMeetActivity.class);
//        intent.setAction("org.jitsi.meet.CONFERENCE");
//        intent.putExtra("JitsiMeetConferenceOptions", options);
//        if (!(context instanceof Activity)) {
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        }
//
//        context.startActivity(intent);
//    }


    @Override
    protected void onStart() {
        super.onStart();
    Toast.makeText(getApplicationContext(),"Hello welcome",Toast.LENGTH_LONG).show();
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT);
//            Button btn = new Button(getApplicationContext());
//            btn.setId(1);
//            final int id_ = btn.getId();
//            btn.setText("button");
//            btn.setBackgroundColor(Color.rgb(70, 80, 90));
//             FrameLayout viewGroup = (FrameLayout) ((ViewGroup) this
//                    .findViewById(android.R.id.content)).getChildAt(0);
//            viewGroup.addView(btn, params);
//            Button btn1 = ((Button) findViewById(1));
//            btn1.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View view) {
//                    Toast.makeText(view.getContext(),
//                            "Button clicked index = " + id_, Toast.LENGTH_SHORT)
//                            .show();
//                }
//            });

        }


        /* protected static final String TAG = org.jitsi.meet.sdk.JitsiMeetActivity.class.getSimpleName();
        private static final String ACTION_JITSI_MEET_CONFERENCE = "org.jitsi.meet.CONFERENCE";
        private static final String JITSI_MEET_CONFERENCE_OPTIONS = "JitsiMeetConferenceOptions";
        private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                 onBroadcastReceived(intent);
            }
        };

        public MyMeetActivity() {
        }

        public static void launch(Context context, JitsiMeetConferenceOptions options) {
            Intent intent = new Intent(context, org.jitsi.meet.sdk.JitsiMeetActivity.class);
            intent.setAction("org.jitsi.meet.CONFERENCE");
            intent.putExtra("JitsiMeetConferenceOptions", options);
            if (!(context instanceof Activity)) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }

            context.startActivity(intent);
        }

        public static void launch(Context context, String url) {
            JitsiMeetConferenceOptions options = (new JitsiMeetConferenceOptions.Builder()).setRoom(url).build();
            launch(context, options);
        }

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.setContentView(R.layout.myactivity);
            this.registerForBroadcastMessages();
            if (!this.extraInitialize()) {
                this.initialize();
            }

        }

        public void onDestroy() {
            this.leave();

            myservice service=new myservice();
            myservice.abort(getApplicationContext());
            LocalBroadcastManager.getInstance(this).unregisterReceiver(this.broadcastReceiver);
            super.onDestroy();
        }

        public void finish() {
            this.leave();
            super.finish();
        }

        protected JitsiMeetView getJitsiView() {
            JitsiMeetFragment fragment = (JitsiMeetFragment)this.getSupportFragmentManager().findFragmentById(R.id.jitsiFragment);
            return fragment != null ? fragment.getJitsiView() : null;
        }

        public void join(@Nullable String url) {
            JitsiMeetConferenceOptions options = (new JitsiMeetConferenceOptions.Builder()).setRoom(url).build();
            this.join(options);
        }

        public void join(JitsiMeetConferenceOptions options) {
            JitsiMeetView view = this.getJitsiView();
            if (view != null) {
                view.join(options);
            } else {
                JitsiMeetLogger.w("Cannot join, view is null", new Object[0]);
            }

        }

        public void leave() {
            JitsiMeetView view = this.getJitsiView();
            if (view != null) {
                view.leave();
            } else {
                JitsiMeetLogger.w("Cannot leave, view is null", new Object[0]);
            }

        }

        @Nullable
        private JitsiMeetConferenceOptions getConferenceOptions(Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.VIEW".equals(action)) {
                Uri uri = intent.getData();
                if (uri != null) {
                    return (new JitsiMeetConferenceOptions.Builder()).setRoom(uri.toString()).build();
                }
            } else if ("org.jitsi.meet.CONFERENCE".equals(action)) {
                return (JitsiMeetConferenceOptions)intent.getParcelableExtra("JitsiMeetConferenceOptions");
            }

            return null;
        }

        protected boolean extraInitialize() {
            return false;
        }

        protected void initialize() {
            this.join(this.getConferenceOptions(this.getIntent()));
        }

        protected void onConferenceJoined(HashMap<String, Object> extraData) {
            JitsiMeetLogger.i("Conference joined: " + extraData, new Object[0]);
     //       JitsiMeetOngoingConferenceService.launch(getApplicationContext());
        }

        protected void onConferenceTerminated(HashMap<String, Object> extraData) {
            JitsiMeetLogger.i("Conference terminated: " + extraData, new Object[0]);
            this.finish();
        }

        protected void onConferenceWillJoin(HashMap<String, Object> extraData) {
            JitsiMeetLogger.i("Conference will join: " + extraData, new Object[0]);
        }

        protected void onParticipantJoined(HashMap<String, Object> extraData) {
            try {
                JitsiMeetLogger.i("Participant joined: ", new Object[]{extraData});
            } catch (Exception var3) {
                JitsiMeetLogger.w("Invalid participant joined extraData", new Object[]{var3});
            }

        }

        protected void onParticipantLeft(HashMap<String, Object> extraData) {
            try {
                JitsiMeetLogger.i("Participant left: ", new Object[]{extraData});
            } catch (Exception var3) {
                JitsiMeetLogger.w("Invalid participant left extraData", new Object[]{var3});
            }

        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            JitsiMeetActivityDelegate.onActivityResult(this, requestCode, resultCode, data);
        }

        public void onBackPressed() {
            JitsiMeetActivityDelegate.onBackPressed();
        }

        public void onNewIntent(Intent intent) {
            super.onNewIntent(intent);
            JitsiMeetConferenceOptions options;
            if ((options = this.getConferenceOptions(intent)) != null) {
                this.join(options);
            } else {
                JitsiMeetActivityDelegate.onNewIntent(intent);
            }
        }

        protected void onUserLeaveHint() {
            JitsiMeetView view = this.getJitsiView();
            if (view != null) {
                view.enterPictureInPicture();
            }

        }

        public void requestPermissions(String[] permissions, int requestCode, PermissionListener listener) {
            JitsiMeetActivityDelegate.requestPermissions(this, permissions, requestCode, listener);
        }

        @SuppressLint("MissingSuperCall")
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            JitsiMeetActivityDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        private void registerForBroadcastMessages() {
            IntentFilter intentFilter = new IntentFilter();
            BroadcastEvent.Type[] var2 = BroadcastEvent.Type.values();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                BroadcastEvent.Type type = var2[var4];
                intentFilter.addAction(type.getAction());
            }

            LocalBroadcastManager.getInstance(this).registerReceiver(this.broadcastReceiver, intentFilter);
        }

        private void onBroadcastReceived(Intent intent) {
            if (intent != null) {
                BroadcastEvent event = new BroadcastEvent(intent);
                switch(event.getType()) {
                    case CONFERENCE_JOINED:
                        this.onConferenceJoined(event.getData());
                        break;
                    case CONFERENCE_WILL_JOIN:
                        this.onConferenceWillJoin(event.getData());
                        break;
                    case CONFERENCE_TERMINATED:
                        this.onConferenceTerminated(event.getData());
                        break;
                    case PARTICIPANT_JOINED:
                        this.onParticipantJoined(event.getData());
                        break;
                    case PARTICIPANT_LEFT:
                        this.onParticipantLeft(event.getData());
                }
            }

        }*/





    String room_name="akki20000";
    public void onInviteClick(View v) {

        createinvitelinks();
    }

    public void createinvitelinks()
    {
        Log.d("domain","click");
        String sharelinktext  = "https://teamsmy.page.link/?"+
                "link=https://www.example.com/?meetid="+room_name+"%"+
                "&apn="+ getPackageName()+
                "&st="+"My Teams"+
                "&sd="+"INVITE LINK!!";

        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(Uri.parse(sharelinktext))  // manually
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();
                            Log.e("main ", "short link "+ shortLink.toString());
                            // share app dialog
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT,  shortLink.toString());
                            intent.setType("text/plain");
                            startActivity(intent);
                        } else {
                            Log.e("main", " error "+task.getException() );
                        }
                    }
                });

    }

}
