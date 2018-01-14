package com.example.inbar.heimdall.Law;

import android.app.Notification;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.inbar.heimdall.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.LogRecord;
import android.os.Message;
import android.os.Handler;

/**
 * Created by Eilon on 05/01/2018.
 */

public class LawListAdapter extends RecyclerView.Adapter<LawListAdapter.SimpleViewHolder> {
    private ArrayList<Law> mLaws;
    protected LawActivity lawActivity;

    private static final int LAWS_UPDATED = 0;

    private Handler listAdapterHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LAWS_UPDATED:
                    LawListAdapter.this.notifyDataSetChanged();
                    break;
            }
        }
    };

    public LawListAdapter(ArrayList<Law> laws, LawActivity father) {
        mLaws = laws;
        lawActivity = father;
    }

    public void getLaws(final Date startDate, final Date endDate) {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                SimpleDateFormat sdfr = new SimpleDateFormat("dd/MM/yyyy");
                JSONObject json = lawActivity.getLawsByDateInterval(R.id.lawLayout, sdfr.format(startDate), sdfr.format(endDate));
                Iterator<?> lawsNames = json.keys();

                mLaws.clear();
                while (lawsNames.hasNext()) {
                    String lawName = (String)lawsNames.next();
                    JSONObject lawDetails = null;
                    try {
                        lawDetails = (JSONObject) json.get(lawName);
                        mLaws.add(new Law(lawName, lawDetails, lawActivity));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

//                for (Law law: mLaws) law.setUserDistAndElectedVotes(lawActivity);
                listAdapterHandler.sendMessage(Message.obtain(listAdapterHandler, LAWS_UPDATED));
            }
        });

        thread.start();
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        int resource = R.layout.single_law_layout;
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_law_layout, parent, false);
        SimpleViewHolder holder = new SimpleViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        final Law law = mLaws.get(position);
        holder.nameTextView.setText(law.getName());
        holder.descriptionTextView.setText(law.getDescription());
        holder.moreInfoButton.setOnClickListener(new MoreInfoButtonListener(law));
    }

    @Override
    public int getItemCount() {
        return mLaws.size();
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {

        protected TextView nameTextView;
        protected TextView descriptionTextView;
        protected Button moreInfoButton;
        protected Button showStatsButton;

        public SimpleViewHolder(View v) {
            super(v);
            nameTextView = ((TextView)v.findViewById(R.id.nameTextView));
            descriptionTextView = ((TextView)v.findViewById(R.id.lawDescriptionTextView));
            moreInfoButton = ((Button)v.findViewById(R.id.moreInfoButton));
            showStatsButton = ((Button)v.findViewById(R.id.showStatsButton));
        }
    }

    public static class VoteButtonListener implements View.OnClickListener {
        private Law mLaw;

        public VoteButtonListener(Law law){
            mLaw = law;
        }
        @Override
        public void onClick(View v) {
            
        }
    }

    public static class MoreInfoButtonListener implements View.OnClickListener {
        private Law mLaw;

        public MoreInfoButtonListener(Law law) {
            mLaw = law;
        }

        @Override
        public void onClick(View v) {
            //the view is the button, we need to get it's parnet
            View parent = (View) v.getParent();
            ExpandableLayout expandableLayout = ((ExpandableLayout) parent.findViewById(R.id.expandable_layout));
            expandableLayout.toggle();
            mLaw.DrawVotesGraph(parent,R.id.VoteLikeMe);
        }
    }

    public static class ShowStatsButtonListener implements View.OnClickListener {
        private Law mLaw;
        private StatisticsPopupMngr mPopupMngr;

        public ShowStatsButtonListener(Law law,StatisticsPopupMngr statPopupMngr) {
            mLaw = law;
            mPopupMngr = statPopupMngr;
        }

        @Override
        public void onClick(View v) {
            //the view is the button, we need to get it's parnet
           mPopupMngr.openPopUp();
        }
    }

    public StatisticsPopupMngr buildPopupClass(){
        Context context = this.lawActivity.getApplicationContext();
        CoordinatorLayout lawPageLayout = this.lawActivity.findViewById(R.id.lawLayout);
        return new StatisticsPopupMngr(context,lawPageLayout);
    }



}

