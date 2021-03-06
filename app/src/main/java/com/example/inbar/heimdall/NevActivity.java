package com.example.inbar.heimdall;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

import com.example.inbar.heimdall.Law.LawActivity;

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

        return super.onOptionsItemSelected(item);
    }
}
