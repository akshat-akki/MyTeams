package com.example.engageteams.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.engageteams.Fragments.TeamsFragment;
import com.example.engageteams.R;
import com.example.engageteams.UI.MeetRooms.WaitingRoom;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import sdk.chat.core.session.ChatSDK;
import sdk.chat.ui.ChatSDKUI;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button meetnowbtn=findViewById(R.id.MeetNowBtn);
        Button teamsbtn=findViewById(R.id.TeamsBtn);
        Button morebtn=findViewById(R.id.MoreBtn);
        meetnowbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(), WaitingRoom.class);
                startActivity(i);

            }
        });

        teamsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent=new Intent(this,ChatSDK.ui().getMainActivity().class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                ChatSDK.ui().startMainActivity(MainActivity.this);
            }
        });
        morebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(), CalenderActivity.class);
                startActivity(i);
            }
        });

    }


    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
