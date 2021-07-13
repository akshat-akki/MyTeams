package com.example.engageteams.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.engageteams.DAO.UserDao;
import com.example.engageteams.Models.User;
import com.example.engageteams.R;
import com.example.engageteams.UI.MeetRooms.WaitingRoom;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import sdk.chat.core.session.ChatSDK;

public class MainActivity extends AppCompatActivity {

    TextView name;
    TextView email;
    TextView phone;
    ImageView profilepic;
    String image_url;
    FirebaseAuth auth=FirebaseAuth.getInstance();
    FirebaseUser currentUser=auth.getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent i=getIntent();
        image_url=i.getStringExtra("Profile_pic_URL");
        name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        phone=findViewById(R.id.phone);
        profilepic=findViewById(R.id.profile_image);
        updateDashboardUI();
        Button meetnowbtn=findViewById(R.id.Meet_Now_btn);
        Button teamsbtn=findViewById(R.id.Teams_btn);
        Button schedulemeetbtn=findViewById(R.id.schedule_meet_btn);
        Button whiteboardbtn=findViewById(R.id.white_board_btn);

       //start meet activity
        meetnowbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(), WaitingRoom.class);
                startActivity(i);

            }
        });

        //start teams chat button
        teamsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent=new Intent(this,ChatSDK.ui().getMainActivity().class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                ChatSDK.ui().startMainActivity(MainActivity.this);

            }
        });

        //start schedule meet activity
        schedulemeetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(), CalenderActivity.class);
                startActivity(i);
            }
        });

        //start whiteboard activity
        whiteboardbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),WhiteBoardActivity.class);
                startActivity(intent);
            }
        });

        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatSDK.ui().startProfileActivity(MainActivity.this,ChatSDK.currentUserID());
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        image_url=ChatSDK.core().currentUser().getAvatarURL();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map imagemap=new HashMap<String,Object>();
        imagemap.put("imageUrl",image_url);
        db.collection("users").document(currentUser.getUid()).update(imagemap);
        updateDashboardUI();
    }

    //updating the dashboard of UI
    void updateDashboardUI()
    {
        UserDao dao=new UserDao();
        Task<DocumentSnapshot> task=dao.getUserById(currentUser.getUid());
        task.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    name.setText(user.getDisplayName());
                    email.setText(user.getEmail());
                    if(user.getPhone().length()>9)
                    {
                        phone.setText(user.getPhone());
                    }
                    Glide.with(getApplicationContext())
                            .load(image_url).optionalCircleCrop()
                            .into(profilepic);
                    // Update their name and avatar
                    ChatSDK.core().currentUser().setAvatarURL(image_url);
                    // Push that data to Firebase
                    ChatSDK.core().pushUser().subscribe(() -> {
                        // Handle success
                    }, throwable -> {
                        // Handle failure
                    });
                }
            }
        });


    }

}
