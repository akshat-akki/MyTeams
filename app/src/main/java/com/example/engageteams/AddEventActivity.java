package com.example.engageteams;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.engageteams.UI.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.tylersuehr.chips.Chip;
import com.tylersuehr.chips.ChipsInputLayout;

import java.util.Calendar;
import java.util.List;

public class AddEventActivity extends AppCompatActivity {

    String title;
    String room_name;
    long calID = 3;
    long startMillis=0;
    long EndMillis=0;
    Calendar startTime;
    Calendar EndTime;
    EditText editTextTitle;
    EditText editStartTime;
    Button createEvent;
    EditText editEndTime;
    EditText editTextdesc;
    ChipsInputLayout chipsInput;
    FirebaseAuth auth=FirebaseAuth.getInstance();
    FirebaseUser currentUser=auth.getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_add__event_);
         Intent i=getIntent();
         createEvent=findViewById(R.id.create_event_btn);
         startTime= (Calendar) i.getSerializableExtra("StartTime");
         EndTime=(Calendar) i.getSerializableExtra("StartTime");
         editTextTitle=findViewById(R.id.edittext_meettitle);
         editTextdesc=findViewById(R.id.edittext_meetdesc);
         editStartTime=findViewById(R.id.edittext_starttime);
         editStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        startTime.set(Calendar.HOUR_OF_DAY,selectedHour);
                        startTime.set(Calendar.MINUTE,selectedMinute);
                        editStartTime.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Start Time");
                mTimePicker.show();

            }
        });
        editEndTime=findViewById(R.id.edittext_endtime);
        editEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        EndTime.set(Calendar.HOUR_OF_DAY,selectedHour);
                        EndTime.set(Calendar.MINUTE,selectedMinute);
                        editEndTime.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select End Time");
                mTimePicker.show();

            }
        });
         chipsInput = (ChipsInputLayout)findViewById(R.id.chips_input);


         room_name=currentUser.getPhoneNumber();
    }
    public void CreateEventClicked(View view)
    {

        title=editTextTitle.getText().toString();
        addEvent();
        Toast.makeText(this,"EVENT ADDED!!",Toast.LENGTH_LONG).show();
        Intent i=new Intent(getApplicationContext(), MainActivity.class);
        i.putExtra("Profile_pic_URL",currentUser.getPhotoUrl());
        startActivity(i);

    }
    private void addEvent()
    {
        startMillis=startTime.getTimeInMillis();
        EndMillis=EndTime.getTimeInMillis();


        List<? extends Chip> contactsSelected = (List<? extends Chip>) chipsInput.getSelectedChips();

        String sharelinktext  = "https://teamsmy.page.link/?"+
                "link=https://www.example.com/?meetid="+room_name+"%"+
                "&apn="+ getPackageName()+
                "&st="+"My Teams"+
                "&sd="+"INVITE LINK!!";

        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(Uri.parse(sharelinktext))  // manually
                .buildShortDynamicLink().addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created

                            Uri shortLink = task.getResult().getShortLink();

                            Log.e("main ", "short link "+ shortLink.toString());
                            ContentResolver cr = getContentResolver();
                            ContentValues values = new ContentValues();
                            values.put(CalendarContract.Events.DTSTART, startMillis);
                            values.put(CalendarContract.Events.DTEND,EndMillis);
                            values.put(CalendarContract.Events.EVENT_TIMEZONE,"Asia/Kolkata");
                            values.put(CalendarContract.Events.TITLE, title);
                            values.put(CalendarContract.Events.DESCRIPTION,editTextdesc.getText().toString()+"\n Meet Link:"+shortLink.toString());
                            values.put(CalendarContract.Events.CALENDAR_ID, calID);
                            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
                            long eventID = Long.parseLong(uri.getLastPathSegment());
                            // get the event ID that is the last element in the Uri
                            for(int i=0;i<contactsSelected.size();i++) {
                                String emailid=contactsSelected.get(i).getTitle().trim();
                                Log.d("chips",emailid);
                                ContentResolver cr2 = getContentResolver();
                                ContentValues valuesatendee = new ContentValues();
                                valuesatendee.put(CalendarContract.Attendees.ATTENDEE_EMAIL, emailid);
                                valuesatendee.put(CalendarContract.Attendees.EVENT_ID, eventID);
                                valuesatendee.put(CalendarContract.Attendees.ATTENDEE_STATUS, CalendarContract.Attendees.ATTENDEE_STATUS_INVITED);
                                Uri uri_atte = cr.insert(CalendarContract.Attendees.CONTENT_URI, valuesatendee);
                            }

                        } else {
                            Log.e("main", " error "+task.getException() );
                        }
                    }
                });
    }
}