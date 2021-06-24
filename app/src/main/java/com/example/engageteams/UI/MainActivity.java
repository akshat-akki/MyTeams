package com.example.engageteams.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

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


        bottomNavigation = findViewById(R.id.bottom_navigation);
        BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.navigation_Teams:
                                openFragment(TeamsFragment.newInstance("", ""));
                                return true;
                            case R.id.navigation_MeetNow:
                                Intent i=new Intent(getApplicationContext(), WaitingRoom.class);
                                startActivity(i);
                                return true;
                        }
                        return false;
                    }
                };

    }
    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
