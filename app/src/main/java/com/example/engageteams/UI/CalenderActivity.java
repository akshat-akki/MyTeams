package com.example.engageteams.UI;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.engageteams.AddEventActivity;
import com.example.engageteams.R;
import com.example.engageteams.UI.MeetRooms.WaitingRoom;

import java.util.Calendar;

public class CalenderActivity extends AppCompatActivity {

    private static final int CALENDER_PERMISSION_CODE = 300;
    Calendar beginTime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        checkPermission(Manifest.permission.READ_CALENDAR,CALENDER_PERMISSION_CODE );

        CalendarView calendarView=(CalendarView) findViewById(R.id.calender_view);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
               beginTime.set(year,month,dayOfMonth);
            }
        });

        Button submitbtn=findViewById(R.id.submitbtn);
        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), AddEventActivity.class);
                intent.putExtra("StartTime",beginTime);
                startActivity(intent);
            }
        });

    }
    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(CalenderActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(CalenderActivity.this, new String[] { permission }, requestCode);
        }
        else {
        }
    }
}