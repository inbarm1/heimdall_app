package com.example.inbar.heimdall.Law;

import android.app.DialogFragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.app.FragmentManager;
import android.view.View;

import com.example.inbar.heimdall.APIRequest;
import com.example.inbar.heimdall.HttpsConnection;
import com.example.inbar.heimdall.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LawActivity extends APIRequest {
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.law_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        mAdapter = new LawListAdapter(getLaws());
        mRecyclerView.setAdapter(mAdapter);

        id_drawer_layout = R.id.drawer_layout_law;
        DrawerLayout drawer = (DrawerLayout) findViewById(id_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new Utils.DatePickerFragment();
        newFragment.show(getFragmentManager(),"datePicker");
    }

    public Law[] getLaws(){
        Law[] laws = new Law[30];//=  ;//Util.getPeopleList(this);
        for (int i =0; i < 30 ; i++){
            Law l1 = null;
            try {
                l1 = createLaw(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            laws[i] = (l1);
        }
        return laws;
    }

    public static Law createLaw(int i) throws JSONException {
        String jsonS = String.format("{'fuck law %s': {'link' : 'www.pornhub.com, 'description' : 'bla bla', 'tags' : ['eilon','ram'], 'user_voted' : 'for'  }",i);
        JSONObject subJson = new JSONObject();
        subJson.put("link","pornhub.com " + String.valueOf(i));
        subJson.put("description","bla bla "+ String.valueOf(i));
        List<String> tags = new ArrayList<>();
        tags.add("eilon "+ String.valueOf(i));
        tags.add("the king "+ String.valueOf(i));
        subJson.put("tags",tags);
        subJson.put("user_voted","for");
        return new Law("law " + String.valueOf(i),subJson );
    }

}





