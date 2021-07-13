package com.example.engageteams;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.engageteams.UI.CalenderActivity;
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
    //declaring global variables
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
    ProgressBar progressBar;
    FirebaseAuth auth=FirebaseAuth.getInstance();
    FirebaseUser currentUser=auth.getCurrentUser();
    boolean setBeginTime=false;
    boolean setEndTime=false;
    boolean setDescription=false;
    boolean setTitle=false;
    private static final int CALENDER_PERMISSION_CODE_2 = 500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_add__event_);

        checkPermission(Manifest.permission.WRITE_CALENDAR,CALENDER_PERMISSION_CODE_2);
         Intent i=getIntent();
         createEvent=findViewById(R.id.create_event_btn);
         startTime= (Calendar) i.getSerializableExtra("StartTime");
         EndTime=(Calendar) i.getSerializableExtra("StartTime");
         editTextTitle=findViewById(R.id.edittext_meettitle);
         editTextdesc=findViewById(R.id.edittext_meetdesc);
         progressBar=findViewById(R.id.calender_delayprogressbar);
         progressBar.setVisibility(View.INVISIBLE);

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
                        startMillis=startTime.getTimeInMillis();
                        setBeginTime=true;
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
                        EndMillis=EndTime.getTimeInMillis();
                        setEndTime=true;
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
        if(editTextTitle.length()>0)
            setTitle=true;
        if(editTextdesc.length()>0)
            setDescription=true;
        if(setBeginTime==true&&setDescription==true&&setEndTime==true&&setTitle==true) {
            progressBar.setVisibility(View.VISIBLE);
            title=editTextTitle.getText().toString();
            addEvent();
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(AddEventActivity.this,"EVENT ADDED!!",Toast.LENGTH_LONG).show();
                    Intent i=new Intent(AddEventActivity.this, MainActivity.class);
                    i.putExtra("Profile_pic_URL",currentUser.getPhotoUrl().toString());
                    startActivity(i);
                }
            }, 3*1000);

        }
        else
        {
            String toast="Please ";
            if(setTitle==false)
                toast+="set Title, ";
            if(setBeginTime==false)
                toast+="set Begin time, ";
            if(setEndTime==false)
                toast+="set End time, ";
            if(setDescription==false)
                toast+="set Description ";
            toast+="for the event";
            Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
        }

    }


    private void addEvent()
    {
        Log.i("startTime","time:"+startMillis);
        Log.i("EndTime","time:"+EndMillis);

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
    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(AddEventActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(AddEventActivity.this, new String[] { permission }, requestCode);
        }
        else {
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}