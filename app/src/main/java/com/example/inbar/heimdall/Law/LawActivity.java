package com.example.inbar.heimdall.Law;

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
import android.view.View;

import com.example.inbar.heimdall.HttpsConnection;
import com.example.inbar.heimdall.R;

import java.util.ArrayList;
import java.util.List;

public class LawActivity extends AppCompatActivity {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.law_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//
//        id_drawer_layout = R.id.drawer_layout_law;
//        DrawerLayout drawer = (DrawerLayout) findViewById(id_drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
//    }

//    public class SimpleListActivity extends AppCompatActivity {

        private RecyclerView.LayoutManager mLayoutManager;
        private RecyclerView.Adapter mAdapter;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setTitle("Law List");
//            mSimpleListBinding = DataBindingUtil.setContentView(
//                    this, R.layout.activity_simple_list);

            List<Law> laws = new ArrayList<Law>();//=  ;//Util.getPeopleList(this);
            Law l1 = new Law("eilon","weinfeld", "king", "bad ass mf", null);
            Law l2 = new Law("eilon1","weinfeld1", "king1", "bad ass mf1", null);
            laws.add(l1);
            laws.add(l2);
            mAdapter = new LawListAdapter(laws);
            mLayoutManager = new LinearLayoutManager(this);
            //mSimpleListBinding.recyclerView.setLayoutManager(mLayoutManager);


            //mSimpleListBinding.recyclerView.setAdapter(mAdapter);
        }
    }




