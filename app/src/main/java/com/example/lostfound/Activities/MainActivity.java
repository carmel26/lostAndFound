package com.example.lostfound.Activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.lostfound.Adapters.ViewPagerAdapter;
import com.example.lostfound.Classes.User;
import com.example.lostfound.Fragments.FoundFragment;
import com.example.lostfound.Fragments.LostFragment;
import com.example.lostfound.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Declare variables

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private SearchView searchView;
    private FloatingActionButton buttonCreate;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;

    private ActionBarDrawerToggle actionBarDrawerToggle;

    private Context context = this;

    private LostFragment lostFrag = new LostFragment();
    private FoundFragment foundFrag = new FoundFragment();

    public static final String POST_ROUTE = "com.example.lostfound.postpage";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

//       check if the user has a profile or not
        databaseReference = FirebaseDatabase.getInstance().getReference("/USERS/" + firebaseAuth.getCurrentUser().getUid()  + "/INFO/");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user == null){
                        Toast.makeText(MainActivity.this, "Please provide your profile information first", Toast.LENGTH_LONG).show();
                        finish();
                        startActivity(new Intent(
                                MainActivity.this,
                                ProfileActivity.class)
                        );
                    }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        notificationPreview("test1","first notification", "And this is my content");

        // Initialize
        searchView = (SearchView) findViewById(R.id.searchView);
        buttonCreate = (FloatingActionButton) findViewById(R.id.buttonCreate);

        toolbar = (Toolbar)findViewById(R.id.toolbar);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationView = (NavigationView)findViewById(R.id.navigation);

        // Set drawer
        setSupportActionBar(toolbar);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.Open,R.string.Close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();

        // Listen to the navigation view, and switch activity based which one clicked
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id) {
                    case R.id.navigation_item_1:
                        startActivity(new Intent(context, ProfileActivity.class));
                        return true;
                    case R.id.navigation_item_2:
                        startActivity(new Intent(context, MessageViewActivity.class));
                        return true;
                    case R.id.navigation_item_3:
                        // Log out
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(context, LoginActivity.class));
                        return true;
                    default:
                        return true;
                }
            }
        });

        // Listen to search and populate the searched item
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if (viewPager.getCurrentItem() == 0){
                    lostFrag.refreshList(newText);
                }
                else if (viewPager.getCurrentItem() == 1) {
                    foundFrag.refreshList(newText);
                }
                return false;
            }
        });

        buttonCreate.setOnClickListener(this);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Add fragments
        adapter.addFragment(lostFrag,"Lost");
        adapter.addFragment(foundFrag,"Found");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == buttonCreate){
            // Create post
            Intent intent = new Intent(this, PostActivity.class);
            if (viewPager.getCurrentItem() == 0){
                intent.putExtra(POST_ROUTE, "LOST");
            }
            else if (viewPager.getCurrentItem() == 1){
                intent.putExtra(POST_ROUTE, "FOUND");
            }
            startActivity(intent);
        }
    }
//
//    public void notificationPreview(String channelID, String title, String content){
//
//        Intent snoozeIntent = new Intent(this, MainActivity.class);
//        snoozeIntent.setAction(Intent.ACTION_SCREEN_ON);
//        snoozeIntent.putExtra(channelID, 0);
//        PendingIntent snoozePendingIntent =
//                PendingIntent.getBroadcast(this, 0, snoozeIntent, 0);
//
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelID)
//                .setSmallIcon(R.drawable.lostandfoundsmalllogo)
//                .setContentTitle(title)
//                .setContentText(content)
//                .setStyle(new NotificationCompat.BigTextStyle()
//                        .bigText(content))
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .addAction(R.drawable.lostandfoundsmalllogo, getString(R.string.app_name),
//                        snoozePendingIntent);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = getString(R.string.app_name);
//            String description = getString(R.string.app_name);
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel(channelID, name, importance);
//            channel.setDescription(description);
//            // Register the channel with the system; you can't change the importance
//            // or other notification behaviors after this
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
////     notificationId is a unique int for each notification that you must define
//        notificationManager.notify(200, builder.build());
//    }
}