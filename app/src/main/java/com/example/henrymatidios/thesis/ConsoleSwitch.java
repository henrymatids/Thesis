package com.example.henrymatidios.thesis;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ConsoleSwitch extends BaseActivity {

    private DatabaseReference dbRef;
    private ImageButton mConsoleSwitch;
    private LinearLayout mPbLinearLayout;
    private String mSwitchStatus;
    private TextView mConsoleNumber;
    private ValueEventListener valueEventListener;

    /*  INTENT EXTRAS   */
    private String CONSOLE_NODE = "EXTRA_CONSOLE_NODE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_console_switch);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_switch);
        setSupportActionBar(toolbar);

        mPbLinearLayout = (LinearLayout) findViewById(R.id.progress_bar_console_switch);
        mConsoleSwitch = (ImageButton) findViewById(R.id.consoleSwitch);
        mConsoleNumber = (TextView) findViewById(R.id.console_number);

        dbRef = Utils.getDatabase(false).getReference("Modules");

        mPbLinearLayout.setVisibility(View.VISIBLE);

        processIntent(getIntent());

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            ImageButton backButton = (ImageButton) findViewById(R.id.switch_back_button);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        processIntent(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        dbRef.removeEventListener(valueEventListener);
    }

    @SuppressWarnings("unchecked")
    public void processIntent(Intent intent) {

        try {
            Bundle extras = intent.getExtras();

            final String console = extras.getString(CONSOLE_NODE);

            if(console != null) {
                mConsoleNumber.setText(console.toUpperCase());

                //GET CONSOLE STATUS FROM THE DATABASE
                valueEventListener = dbRef.child(console).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, String> mStatus = (Map<String, String>) dataSnapshot.getValue();

                        if (mStatus != null) {
                            mSwitchStatus = mStatus.get("status");
                            setSwitch(mSwitchStatus);
                        }
                        mPbLinearLayout.setVisibility(View.GONE);
                        mConsoleNumber.setVisibility(View.VISIBLE);
                        mConsoleSwitch.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

        } catch(Exception e) {
            Log.e("Error: ", e.getMessage());
        }
    }

    public void setSwitch(String status) {
        if(status.equals("On")) {
            mConsoleSwitch.setBackgroundResource(R.mipmap.ic_power_on);
        } else {
            mConsoleSwitch.setBackgroundResource(R.mipmap.ic_power_off);
        }

        mConsoleSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*  CHECK IF THERE IS AN INTERNET CONNECTION    */
                if(Utils.isDataConnectionAvailable(getApplicationContext())){

                    showProgressDialog();

                    if(mSwitchStatus.equals("On")){
                        updateSwitch(getIntent());
                        mConsoleSwitch.setBackgroundResource(R.mipmap.ic_power_on);
                    } else if(mSwitchStatus.equals("Off")) {
                        updateSwitch(getIntent());
                        mConsoleSwitch.setBackgroundResource(R.mipmap.ic_power_off);
                    }
                } else {
                    Toast.makeText(ConsoleSwitch.this, "Internet connection needed", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public void updateSwitch(Intent intent) {
        final String console;

        try{
            Bundle extras = intent.getExtras();

            console = extras.getString(CONSOLE_NODE);

            final Map<String, String> inputVal = new HashMap<>();
            inputVal.clear();
            if(mSwitchStatus.equals("On")){
                inputVal.put("status","Off");
            } else if(mSwitchStatus.equals("Off")) {
                inputVal.put("status", "On");
            }

            if(console != null) {
                Map<String, Object> childUpdate = new HashMap<>();
                childUpdate.put("/"+console+"/", inputVal);

                //UPDATE THE STATUS OF THE CONSOLE IN THE DATABASE
                dbRef.updateChildren(childUpdate, new DatabaseReference.CompletionListener() {

                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        Toast.makeText(ConsoleSwitch.this, inputVal.get("status").toUpperCase(), Toast.LENGTH_SHORT).show();
                        hideProgressDialog();
                    }
                });
            }

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
            hideProgressDialog();
        }
    }
}
