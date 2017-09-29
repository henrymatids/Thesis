package com.example.henrymatidios.thesis;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.app.Notification.DEFAULT_SOUND;

/**
 * @author Henry Matidios
 * @since 27/09/2017.
 */

public class NotificationService extends JobService {

    private int unreadNotif;
    private List<String> notifList;

    public NotificationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notifList = new ArrayList<>();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        return START_NOT_STICKY;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean onStartJob(JobParameters params) {
        unreadNotif = 0;
        notifList.clear();

        DatabaseReference dbRef = Utils.getDatabase(true).getReference("Notification");
        dbRef.keepSynced(true);

        dbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Map<String, String> mData = (Map<String, String>) dataSnapshot.getValue();

                if(mData != null)
                {
                    if (mData.get("processed").equals("false")) {
                        showNotification(mData.get("location"), dataSnapshot.getKey());
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    public void showNotification(String location, String key){


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());

        mBuilder.setSmallIcon(R.mipmap.ic_alert)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_alert))
                .setTicker("Gas Leak Alert!")
                .setAutoCancel(true)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000})
                .setDefaults(DEFAULT_SOUND)
                .setContentTitle("Gas Leak Alert!")
                .setContentText(location)
                .setGroup("gas_leak_alert")
                .setGroupSummary(true);

        notifList.add(location);
        unreadNotif++;

        if(unreadNotif > 1) {
            NotificationCompat.InboxStyle notifStyle = new NotificationCompat.InboxStyle();
            notifStyle.setBigContentTitle("Gas Leak Alert")
                    .setSummaryText("Gas Leak");

            for(int i = 0; i < notifList.size(); i++) {
                notifStyle.addLine(notifList.get(i));
            }

            mBuilder.setStyle(notifStyle);
        }

//        Intent notificationIntent = new Intent(this, LogsActivity.class);
//        notificationIntent.putExtra("EXTRA_NOTIFICATION_CLICKED", true);
//        notificationIntent.putExtra("EXTRA_NOTIFICATION_KEY", key);
//
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        mBuilder.setContentIntent(contentIntent);
//
//        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        manager.notify(1, mBuilder.build());
    }
}