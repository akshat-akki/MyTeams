package com.example.engageteams.UI.MeetRooms;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.engageteams.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import org.jitsi.meet.sdk.BroadcastEvent;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetActivityDelegate;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetView;
import org.jitsi.meet.sdk.log.JitsiMeetLogger;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import sdk.chat.core.dao.Message;
import sdk.chat.core.events.EventType;
import sdk.chat.core.events.NetworkEvent;
import sdk.chat.core.session.ChatSDK;


public class MyMeetActivity extends JitsiMeetActivity {
    private static String room_name;
    private JitsiMeetView view;
    FrameLayout videoView;
    boolean gone = false;
    Button chatbtn;
    CardView cardView;
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            MyMeetActivity.this.onBroadcastReceived(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.myactivity);
        view = new JitsiMeetView(MyMeetActivity.this);
         videoView = (FrameLayout) findViewById(R.id.meetView);
         cardView=findViewById(R.id.card_view_meet);
          chatbtn=findViewById(R.id.chat_btn);
        this.registerForBroadcastMessages();
        if (!this.extraInitialize()) {
            this.initialize();
        }
        Disposable d = ChatSDK.events().sourceOnMain()
                .filter(NetworkEvent.filterType(EventType.MessageAdded))
                .subscribe(new Consumer<NetworkEvent>() {
                    @Override
                    public void accept(NetworkEvent networkEvent) {
                        Message message = networkEvent.getMessage();
                        chatbtn.setForeground(getResources().getDrawable(R.drawable.messege_chat_btn));
                    }
                    public void onError(Exception e)
                    {
                        e.printStackTrace();
                    }
                });
    }
    @SuppressLint("WrongConstant")
    public static void launch(Context context, JitsiMeetConferenceOptions options) {
        Intent intent = new Intent(context, MyMeetActivity.class);
        intent.setAction("org.jitsi.meet.CONFERENCE");
        intent.putExtra("JitsiMeetConferenceOptions", options);
        room_name=options.getRoom();
        if (!(context instanceof Activity)) {
            intent.setFlags(268435456);
        }

        context.startActivity(intent);
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
    public void join(JitsiMeetConferenceOptions options) {

        if (view != null) {
            view.join(options);
            videoView.addView(view);
            cardView.setVisibility(View.VISIBLE);
        } else {
            JitsiMeetLogger.w("Cannot join, view is null", new Object[0]);
        }

    }
    public void chatClicked(View v)
    {
        chatbtn.setForeground(getResources().getDrawable(R.drawable.chat_btn_normal));
        ChatSDK.ui().startMainActivity(MyMeetActivity.this);
    }
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

    }
    public void inviteClicked(View btnview)
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

    public void onDestroy() {
        super.onDestroy();
        JitsiMeetActivityDelegate.onHostDestroy(this);
    }

    public void onResume() {
        super.onResume();
        JitsiMeetActivityDelegate.onHostResume(this);
    }

    public void onStop() {
        super.onStop();
        JitsiMeetActivityDelegate.onHostPause(this);
    }

    @Override
    public void onBackPressed() {
        view.enterPictureInPicture();
    }
}
