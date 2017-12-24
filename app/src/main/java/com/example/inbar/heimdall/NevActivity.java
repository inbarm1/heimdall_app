package com.example.inbar.heimdall;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.mikephil.charting.charts.PieChart;

public class NevActivity extends FirebaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected int id_drawer_layout = R.id.drawer_layout;

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Intent intent = new Intent(NevActivity.this,ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_personal_statistics) {

        } else if (id == R.id.nav_laws) {
            Intent intent = new Intent(NevActivity.this,LawActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

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
