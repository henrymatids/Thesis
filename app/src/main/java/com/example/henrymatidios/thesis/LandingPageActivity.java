package com.example.henrymatidios.thesis;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.henrymatidios.thesis.Models.Logs;
import com.example.henrymatidios.thesis.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class LandingPageActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, WelcomeFragment.OnFragmentInteractionListener, AccountFragment.OnListFragmentInteractionListener, FabFragment.OnFragmentInteractionListener, LogsFragment.OnListFragmentInteractionListener {

    private FirebaseAuth mAuth;
    private Intent myServiceIntent;
    private String accountType = "0";       //Account type of the logged in account. 0 if admin and 1 if user.
    private TextView toolbarTitle;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        //Firebase instance
        mAuth = FirebaseAuth.getInstance();
        getUserCredentials();

        dbRef = Utils.getDatabase(false).getReference("Notification");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.welcome_message);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!= null)
            actionBar.setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View view = navigationView.getHeaderView(0);
        TextView displayName = (TextView) view.findViewById(R.id.nav_display_name);
        TextView email = (TextView) view.findViewById(R.id.nav_email);
        ImageView displayPicture = (ImageView) view.findViewById(R.id.nav_display_picture);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
//            displayPicture.setImageResource();        //set account's profile picture
            displayName.setText(user.getDisplayName());
            email.setText(user.getEmail());
        }
        navigationView.setNavigationItemSelectedListener(this);

        createService();

//        if(getApplicationContext() instanceof NotificationService) {
//            LogsFragment newFragment = new LogsFragment();
//
//            toolbarTitle.setText(R.string.logs_indicator);
//
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            transaction.replace(R.id.fragment_container, newFragment);
//            transaction.commit();
//        } else {
            WelcomeFragment newFragment = new WelcomeFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container, newFragment);
            transaction.commit();
//        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.landing_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_switch) {
            showProgressDialog();
            Intent intent = new Intent(getApplicationContext(), ConsoleSwitch.class);
            intent.putExtra("EXTRA_CONSOLE_NODE","main");
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_home) {

            WelcomeFragment newFragment = new WelcomeFragment();
            toolbarTitle.setText(R.string.welcome_message);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.commit();

        } else if (id == R.id.nav_account) {

            AccountFragment newFragment = new AccountFragment();

            FabFragment fabFragment = new FabFragment();
            toolbarTitle.setText(R.string.account_message);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.add(R.id.fragment_container, fabFragment);
            transaction.commit();

        } else if (id == R.id.nav_logs) {

            LogsFragment newFragment = new LogsFragment();

            toolbarTitle.setText(R.string.logs_indicator);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.commit();

        }  else if (id == R.id.nav_about) {

            Toast.makeText(this, "ABAWT", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_logout) {

            mAuth.signOut();

            if (myServiceIntent != null) {
                stopService(myServiceIntent);
            }

            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressWarnings("unchecked")
    public void getUserCredentials() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null) {
            String userID = user.getUid();
            DatabaseReference dbRef = Utils.getDatabase(true).getReference("Accounts");

            dbRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    HashMap<String, ?> snapshotValue = (HashMap<String, ?>) dataSnapshot.getValue();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if(snapshotValue != null){
                        accountType = (String)snapshotValue.get("type");
                        UserProfileChangeRequest userProfile = new UserProfileChangeRequest.Builder()
                                .setDisplayName(snapshotValue.get("name").toString())
                                .build();
                        if(user != null) {
                            user.updateProfile(userProfile);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    /**
     * Notification Service
     */
    public void createService() {
        //Start notification service
        ComponentName myServiceComponent = new ComponentName(getApplicationContext(), NotificationService.class);
        myServiceIntent = new Intent(getApplicationContext(), NotificationService.class);
        startService(myServiceIntent);

        //Schedule notification service
        JobInfo.Builder mBuilder = new JobInfo.Builder(0, myServiceComponent);
        mBuilder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);

        JobScheduler mJobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        mJobScheduler.schedule(mBuilder.build());
    }

    /**
     * WelcomeFragment Callback
     * @param position
     */
    @Override
    public void onFragmentInteraction(int position) {
        Toast.makeText(this, "Position Value" + position, Toast.LENGTH_SHORT).show();
    }

    /**
     * LogsFragment Callback
     * @param item
     */
    @Override
    public void onListFragmentInteraction(View view, Logs item) {
        showLogsPopupMenu(view, item.values.getLocation());
    }

    /**
     * FabFragment Callback
     * @param uri
     */
    @Override
    public void onFragmentInteraction(Uri uri) {
        Toast.makeText(this, "FRAGMENT BUTTON CLICKED", Toast.LENGTH_SHORT).show();

    }

    /**
     * AccountsFragment Callback
     * @param info
     */
    @Override
    public void onListFragmentInteraction(View view, User info) {
        showAccountPopupMenu(view, " ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(myServiceIntent);
    }
    public void showLogsPopupMenu(final View view, final String value){

        PopupMenu popup = new PopupMenu(getApplicationContext(), view);

        popup.getMenuInflater().inflate(R.menu.logs_popup_menu, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action:
                        showProgressDialog();
                        markAsRead(view);
                        Intent intent = new Intent(getApplicationContext(), ConsoleSwitch.class);
                        intent.putExtra("EXTRA_CONSOLE_NODE",value);
                        startActivity(intent);
                        break;
                    case R.id.ignore:
                        showProgressDialog();
                        markAsRead(view);
                        Toast.makeText(LandingPageActivity.this, "Ignored", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    public void markAsRead(View view) {

        ImageView mImage = (ImageView) view.findViewById(R.id.imageView);
        TextView mKey = (TextView) view.findViewById(R.id.logs_key);
        TextView mLocation = (TextView) view.findViewById(R.id.location_editText);
        TextView mDate = (TextView) view.findViewById(R.id.date_editText);
        TextView mTime = (TextView) view.findViewById(R.id.time_editText);

        mImage.setImageResource(R.mipmap.ic_greencircle);
        String key = mKey.getText().toString();
        String location = mLocation.getText().toString();
        String date = mDate.getText().toString();
        String time = mTime.getText().toString();


        Map<String, String> mObj = new HashMap<>();
        mObj.put("date", date);
        mObj.put("location", location);
        mObj.put("processed", "true");
        mObj.put("time", time);

        Map<String, Object> childUpdate = new HashMap<>();
        childUpdate.put("/"+key+"/", mObj);

        dbRef.updateChildren(childUpdate, new DatabaseReference.CompletionListener(){
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                hideProgressDialog();
            }
        });
    }

    public void showAccountPopupMenu(View view, final String userID){

        PopupMenu popup = new PopupMenu(getApplicationContext(), view);

        popup.getMenuInflater().inflate(R.menu.view_users_popup_menu, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete:
                        Toast.makeText(getApplicationContext(), "Delete", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.details:
                        Toast.makeText(getApplicationContext(), "Details", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }
}
