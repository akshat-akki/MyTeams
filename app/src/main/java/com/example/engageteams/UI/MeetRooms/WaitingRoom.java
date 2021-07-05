package com.example.engageteams.UI.MeetRooms;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.engageteams.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;



public class WaitingRoom extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int RECORD_PERMISSION_CODE = 102;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private String room_name="";
    PreviewView previewView;
    ProcessCameraProvider cameraProvider;
    Preview preview;
    EditText meet_id;
    int mic=1;
    int cam=1;
    Button micbtn;
    Button cambtn;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);

        getLink();

        checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);
        checkPermission(Manifest.permission.RECORD_AUDIO, RECORD_PERMISSION_CODE);

        previewView = findViewById(R.id.PreviewCamera);
        Button join=findViewById(R.id.Join_Meet_Button);
        micbtn=findViewById(R.id.microphone_button);
        cambtn=findViewById(R.id.camera_button);
         meet_id=findViewById(R.id.meet_id);

        Button create=findViewById(R.id.Create_Meet_Button);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i=new Intent(getApplicationContext(),MeetNow.class);
//                i.setAction( "org.jitsi.meet.CONFERENCE");
//                i.putExtra("Meet_ID",meet_id.getText().toString());
//                startActivity(i);
                JitsiMeetConferenceOptions options = null;
                try {
                    options = new JitsiMeetConferenceOptions.Builder()
                            .setServerURL(new URL("https://meet.jit.si"))
                            .setRoom("akki20000")
                            .setVideoMuted(true)
                            .setAudioMuted(true)
                            .setFeatureFlag("invite.enabled",false)
                            .setFeatureFlag("pip.enabled",true)
                            .setFeatureFlag("lobby-mode.enabled",false)
                            .build();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                MyMeetActivity.launch(getApplicationContext(), options);
            }
        });


        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent i=new Intent(getApplicationContext(),MeetNow.class);
               i.putExtra("Meet_ID",meet_id.getText().toString());
               startActivity(i);

            }
        });
        startcamera();

    }
    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(WaitingRoom.this, permission) == PackageManager.PERMISSION_DENIED) {
        ActivityCompat.requestPermissions(WaitingRoom.this, new String[] { permission }, requestCode);
        }
        else {
            //Toast.makeText(MainActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
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

    public void micClicked(View view)
    {
        if(mic==1)
        {
            Drawable myDrawable = getResources().getDrawable(R.drawable.ic_baseline_mic_off);
              micbtn.setForeground(myDrawable);
              mic=0;
        }
        else
        {
            Drawable myDrawable = getResources().getDrawable(R.drawable.ic_baseline_mic);
            micbtn.setForeground(myDrawable);
            mic=1;
        }
    }
    public void camClicked(View view)
    {
        if(cam==1)
        {
            Drawable camDrawable = getResources().getDrawable(R.drawable.ic_baseline_videocam_off_24);
            cambtn.setForeground(camDrawable);
            cameraProvider.unbindAll();
            previewView.setForeground(getResources().getDrawable(R.color.black));
            cam=0;

        }
        else
        {
            Drawable myDrawable = getResources().getDrawable(R.drawable.ic_baseline_videocam);
            cambtn.setForeground(myDrawable);
            previewView.setBackgroundResource(0);
            previewView.setForeground(null);
            bindPreview(cameraProvider);
            cam=1;
        }
    }
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


                        // Handle the deep link. For example, open the linked
                        // content, or apply promotional credit to the user's
                        // account.
                        // ...

                        // ...
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("failed to get link", "getDynamicLink:onFailure", e);
                    }
                });

    }
}
