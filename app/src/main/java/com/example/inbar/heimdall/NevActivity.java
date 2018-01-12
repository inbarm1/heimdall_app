package com.example.inbar.heimdall;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.inbar.heimdall.Law.Law;
import com.example.inbar.heimdall.Law.LawActivity;

public class NevActivity extends FirebaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected int id_drawer_layout = R.id.drawer_layout;

    public void sendNotification(View view) {
        String CHANNEL_ID = "my_channel_01";
        String CHANNEL_NAME = "my Channel Name";
        int NOTIFICATION_ID = 1;

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationChannel notificationChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            notificationChannel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        // prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(this, LawActivityOld.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification myNotification = new NotificationCompat.Builder(NevActivity.this, CHANNEL_ID)
                .setContentTitle("You have been notify")
                .setContentText("This is your Notifiaction Text")
                .setSmallIcon(R.drawable.ic_person_white)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .setSound(alarmSound)
                .build();

        notificationManager.notify(NOTIFICATION_ID, myNotification);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Intent intent = new Intent(NevActivity.this,ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_personal_statistics) {
            Intent intent = new Intent(NevActivity.this,PersonalStatisticsActivity.class);
            startActivity(intent);
        }  else if (id == R.id.nav_general_statistics) {
            Intent intent = new Intent(NevActivity.this,MainActivity.class);
            startActivity(intent);
        }else if (id == R.id.nav_laws) {
            Intent intent = new Intent(NevActivity.this, LawActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_log_out){
            // Firebase sign out
            mAuth.signOut();
            // Google sign out
            mGoogleSignInClient.signOut();
            Intent intent = new Intent(NevActivity.this,SignInActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(id_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(id_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // TODO NOTIFICATION
//    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
//    public void sendNotification(View view) {
//
//        // prepare intent which is triggered if the
//        // notification is selected
//
//        Intent intent = new Intent(NevActivity.this, NotificationActivity.class);
//        // use System.currentTimeMillis() to have a unique ID for the pending intent
//        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
//
//        // build notification
//        // the addAction re-use the same intent to keep the example short
//        Notification n  = new Notification.Builder(this)
//                .setContentTitle("New mail from " + "test@gmail.com")
//                .setContentText("Subject")
//                .setSmallIcon(R.drawable.ic_person_white)
//                .setContentIntent(pIntent)
//                .setAutoCancel(true).build();
////                .addAction(R.drawable.ic_person_white, "Call", pIntent)
////                .addAction(R.drawable.ic_person_white, "More", pIntent)
////                .addAction(R.drawable.ic_person_white, "And more", pIntent).build();
//
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//        notificationManager.notify(0, n);
//    }
}
