package com.example.lostfound.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.lostfound.Adapters.PostAdapter;
import com.example.lostfound.Classes.Post;
import com.example.lostfound.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SplashScreen extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private ListView listView;
    private List<Post> postList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getDataFromOnline();
//        notificationPreview("test1","first notification", "And this is my content");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }

    public void getDataFromOnline(){

        postList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("/LOST");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int numberLost=0;
                postList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    Post post = postSnapshot.child("INFO").getValue(Post.class);

                    if (post.getStatus().equalsIgnoreCase("false") && numberLost < 1){
                        numberLost = numberLost +1;
                        postList.add(post);
                        notificationPreview(post.getPostId(),"LOST: "+post.getTitle(), post.getDescription());
                    }

                }
//                Collections.reverse(postList);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        databaseReference = FirebaseDatabase.getInstance().getReference("/FOUND");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int numberFound = 0;
                postList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    Post post = postSnapshot.child("INFO").getValue(Post.class);

                    if (post.getStatus().equalsIgnoreCase("false") && numberFound < 1){
                        numberFound = numberFound+1;
                        postList.add(post);
                        notificationPreview(post.getPostId(),"FOUND: "+post.getTitle(), post.getDescription());
                    }

                }
//                Collections.reverse(postList);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public void notificationPreview(String channelID, String title, String content){

        Intent snoozeIntent = new Intent(this, MainActivity.class);
        snoozeIntent.setAction(Intent.ACTION_SCREEN_ON);
        snoozeIntent.setAction("Test " + System.currentTimeMillis());
        snoozeIntent.putExtra(channelID, 0);
        PendingIntent snoozePendingIntent =
                PendingIntent.getBroadcast(this, 0, snoozeIntent, 0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelID)
                .setSmallIcon(R.drawable.lostandfoundsmalllogo)
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(content))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(R.drawable.lostandfoundsmalllogo, "OK",
                        snoozePendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            assert notificationManager != null;
            notificationManager.notify(( int ) System. currentTimeMillis () ,
                    builder.build()) ;
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//     notificationId is a unique int for each notification that you must define
        notificationManager.notify(200, builder.build());
    }
}