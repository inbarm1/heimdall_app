package com.example.inbar.heimdall.Law;

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

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Eilon on 05/01/2018.
 */

public class LawListAdapter extends RecyclerView.Adapter<LawListAdapter.SimpleViewHolder> {
    private ArrayList<Law> mLaws;
    protected LawActivity lawActivity;
    private StatisticsPopupMngr mStatPopupMngr;

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
                HashMap<String, JSONObject> laws = new Gson().fromJson(json.toString(),
                        new TypeToken<HashMap<String, JSONObject>>() {}.getType());
                mLaws.clear();
                for (HashMap.Entry<String, JSONObject> entry: laws.entrySet()) {
                    String lawName = entry.getKey();
                    JSONObject lawDetails = entry.getValue();
                    mLaws.add(new Law(lawName, lawDetails, lawActivity));
                }

                for (Law law: mLaws) law.setUserDistAndElectedVotes(lawActivity);
            }
        });

        thread.start();

        while (thread.isAlive()) {};

        this.notifyDataSetChanged();
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

