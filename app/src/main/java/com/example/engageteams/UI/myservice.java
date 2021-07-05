package com.example.engageteams.UI;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import org.jitsi.meet.sdk.JitsiMeetOngoingConferenceService;
import org.jitsi.meet.sdk.log.JitsiMeetLogger;

public class myservice extends JitsiMeetOngoingConferenceService  {

    public static void abort(Context context) {
        Intent intent = new Intent(context, JitsiMeetOngoingConferenceService.class);
        context.stopService(intent);
    }

}
