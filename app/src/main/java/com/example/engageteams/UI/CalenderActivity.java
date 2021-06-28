package com.example.engageteams.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.engageteams.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

public class CalenderActivity extends AppCompatActivity {
    String title="my meet";
    String room_name="myroom";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        Button addeventbtn=findViewById(R.id.addeventbtn);

        addeventbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             createlink();
            }
        });

    }
    private void createlink()
    {
        String sharelinktext  = "https://teamsmy.page.link/?"+
                "link=https://www.example.com/?meetid="+room_name+"%"+
                "&apn="+ getPackageName()+
                "&st="+"My Teams"+
                "&sd="+"INVITE LINK!!";

        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(Uri.parse(sharelinktext))  // manually
                .buildShortDynamicLink()        .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created

                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();
                            Log.e("main ", "short link "+ shortLink.toString());
                            Intent intent = new Intent(Intent.ACTION_INSERT)
                                    .setData(CalendarContract.Events.CONTENT_URI)
                                    .putExtra(CalendarContract.Events.TITLE, title)
                                    .putExtra(Intent.EXTRA_EMAIL,"manisha.akshat1976@gmail.com,akshat.ic19@nsut.ac.in")
                                    .putExtra(CalendarContract.Events.DESCRIPTION, "Link:"+shortLink.toString());


                            if(intent.resolveActivity(getPackageManager())!=null) {
                                startActivity(intent);
                            }
                            else
                            {
                                Log.i("response","EROR");
                            }

                        } else {
                            Log.e("main", " error "+task.getException() );
                        }
                    }
                });
    }
}