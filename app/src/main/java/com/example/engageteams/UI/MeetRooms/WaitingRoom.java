package com.example.engageteams.UI.MeetRooms;

import android.Manifest;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.engageteams.R;
import com.example.engageteams.UI.SplashActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import sdk.chat.core.session.ChatSDK;

/*
    Waiting Room Activity before entering the real meet
*/
public class WaitingRoom extends AppCompatActivity {

    //declaring global variables
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int RECORD_PERMISSION_CODE = 102;
    private String room_name="";
    boolean urlintentcalled=false;
    PreviewView previewView;
    ProcessCameraProvider cameraProvider;
    Preview preview;
    EditText meet_id;
    boolean micmuted=false;
    boolean cammuted=false;
    Button micbtn;
    Button cambtn;
    Button viewEvents;
    JitsiMeetConferenceOptions options = null;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);

         getLink();

         checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);
         checkPermission(Manifest.permission.RECORD_AUDIO, RECORD_PERMISSION_CODE);
         TextView warning=findViewById(R.id.warning_text_view);
         previewView = findViewById(R.id.PreviewCamera);
         viewEvents=findViewById(R.id.View_Events_Button);
         micbtn=findViewById(R.id.microphone_button);
         cambtn=findViewById(R.id.camera_button);
         meet_id=findViewById(R.id.meet_id);

         //function call when create button clicked
         Button create=findViewById(R.id.Enter_Meet_Button);
         create.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if (meet_id.getText().toString().length()>0) {
                     room_name=meet_id.getText().toString().trim();
                     warning.setVisibility(View.INVISIBLE);

                     try {
                          options = new JitsiMeetConferenceOptions.Builder()
                                 .setServerURL(new URL("https://meet.jit.si"))
                                 .setRoom(room_name)
                                 .setVideoMuted(cammuted)
                                 .setAudioMuted(micmuted)
                                  .setFeatureFlag("chat.enabled",false)
                                 .setFeatureFlag("invite.enabled", false)
                                 .setFeatureFlag("pip.enabled", true)
                                 .build();
                          showdailog();
                        }
                     catch (MalformedURLException e) {
                         e.printStackTrace();
                     }

                 }
                 else {
                     warning.setVisibility(View.VISIBLE);
                 }
             }
        });


        //view events button clicked
        viewEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long startMillis = System.currentTimeMillis();
                Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
                builder.appendPath("time");
                ContentUris.appendId(builder, startMillis);
               Intent intent = new Intent(Intent.ACTION_VIEW).setData(builder.build());
                startActivity(intent);
            }
        });
        startcamera();

    }

    //function to show dailog box before entering the meet
    public void showdailog()
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(WaitingRoom.this);
        builder1.setTitle("SHARE MEETING");
        builder1.setMessage("Meet ID "+room_name);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Share Link",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        createinvitelinks();
                    }
                });

        builder1.setNegativeButton(
                "Enter Meet",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            MyMeetActivity.launch(WaitingRoom.this,options);
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(getApplicationContext(),
                                    e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


    // function the get user permissions
    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(WaitingRoom.this, permission) == PackageManager.PERMISSION_DENIED) {
        ActivityCompat.requestPermissions(WaitingRoom.this, new String[] { permission }, requestCode);
        }
        else {
        }
    }

    //generating invite links
    private void createinvitelinks()
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

    private void startcamera()
    {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                 cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));
    }
    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        try {
            cameraProvider.unbindAll();
            cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //toggling mic clicked
    public void micClicked(View view)
    {
        if(micmuted==false)
        {
            Drawable myDrawable = getResources().getDrawable(R.drawable.ic_baseline_mic_off);
              micbtn.setForeground(myDrawable);
              micmuted=true;
        }
        else
        {
            Drawable myDrawable = getResources().getDrawable(R.drawable.ic_baseline_mic);
            micbtn.setForeground(myDrawable);
            micmuted=false;
        }
    }

    //toggling cam clicked
    public void camClicked(View view)
    {
        if(cammuted==false)
        {
            Drawable camDrawable = getResources().getDrawable(R.drawable.ic_baseline_videocam_off_24);
            cambtn.setForeground(camDrawable);
            cameraProvider.unbindAll();
            previewView.setForeground(getResources().getDrawable(R.color.black));
            cammuted=true;

        }
        else
        {
            Drawable myDrawable = getResources().getDrawable(R.drawable.ic_baseline_videocam);
            cambtn.setForeground(myDrawable);
            previewView.setBackgroundResource(0);
            previewView.setForeground(null);
            bindPreview(cameraProvider);
            cammuted=false;
        }
    }

    //getting the link when user enters the meet from invite URL
    public void getLink()
    {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            urlintentcalled=true;
                            viewEvents.setVisibility(View.INVISIBLE);
                            deepLink = pendingDynamicLinkData.getLink();
                            String referlink=deepLink.toString();
                            try {
                                referlink=referlink.substring(referlink.lastIndexOf("=")+1);
                                room_name=referlink.substring(0,referlink.indexOf("%"));

                                    meet_id.setText(room_name);
                                
                                Log.d("ROOMNAME",room_name);
                            }
                            catch (Exception e)
                            {
                               e.printStackTrace();
                            }
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("failed to get link", "getDynamicLink:onFailure", e);
                    }
                });

    }

    @Override
    public void onBackPressed() {
        if(urlintentcalled==false)
        super.onBackPressed();
        else
        {
            Intent i=new Intent(getApplicationContext(), SplashActivity.class);
            startActivity(i);
        }
    }
}
