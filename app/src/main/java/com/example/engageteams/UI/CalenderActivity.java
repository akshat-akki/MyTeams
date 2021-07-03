package com.example.engageteams.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.engageteams.AddEventActivity;
import com.example.engageteams.R;

import java.util.Calendar;

public class CalenderActivity extends AppCompatActivity {

    long startMillis = 0;
    Calendar beginTime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);


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
}