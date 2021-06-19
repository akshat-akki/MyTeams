package com.example.engageteams.UI.MeetRooms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;

import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.engageteams.R;
import com.google.common.util.concurrent.ListenableFuture;


import java.util.concurrent.ExecutionException;



public class WaitingRoom extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int RECORD_PERMISSION_CODE = 102;
    private static final int STORAGE_PERMISSION_CODE = 101;

    PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);

        checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);
        checkPermission(Manifest.permission.RECORD_AUDIO, RECORD_PERMISSION_CODE);

        previewView = findViewById(R.id.PreviewCamera);
        Button join=findViewById(R.id.Join_Meet_Button);
        EditText meet_id=findViewById(R.id.meet_id);
        Button create=findViewById(R.id.Create_Meet_Button);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),MeetNow.class);
                i.putExtra("Meet_ID",meet_id.getText().toString());
                startActivity(i);
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
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));
    }
    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        Camera camera = (Camera) cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview);
    }

}
