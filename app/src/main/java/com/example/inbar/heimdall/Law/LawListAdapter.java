package com.example.inbar.heimdall.Law;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.inbar.heimdall.R;
import com.example.inbar.heimdall.UserVote;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import android.os.Message;
import android.os.Handler;

/**
 * Created by Eilon on 05/01/2018.
 */

public class LawListAdapter extends RecyclerView.Adapter<LawListAdapter.SimpleViewHolder> {
    private ArrayList<Law> mLaws;
    protected LawActivity lawActivity;
    private StatisticsPopupMngr mStatsPopupMngr;

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
        mStatsPopupMngr = buildStatsPopupMngr();
    }

    public void getLaws(final Calendar startC, final Calendar endC) {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                Date startDate = startC.getTime();
                Date endDate = endC.getTime();

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        final Law law = mLaws.get(position);
        holder.nameTextView.setText(law.getName());
        holder.moreInfoButton.setOnClickListener(new MoreInfoButtonListener(law,lawActivity) );
        Drawable voteIcon = lawActivity.getDrawable(law.getLawVoteIconDrawableId());
        holder.moreInfoButton.setCompoundDrawablesWithIntrinsicBounds(voteIcon,null,null,null);
        holder.showStatsButton.setOnClickListener( new ShowStatsButtonListener(law,mStatsPopupMngr));
        holder.showDescriptionButton.setOnClickListener( new ShowDescriptionButtonListener(law,mStatsPopupMngr));
        holder.voteButton.setOnClickListener(new VoteButtonListener(law,mStatsPopupMngr));
    }

    @Override
    public int getItemCount() {
        return mLaws.size();
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {

        protected TextView nameTextView;
        protected Button moreInfoButton;
        protected Button showStatsButton;
        protected Button showDescriptionButton;
        protected Button voteButton;


        public SimpleViewHolder(View v) {
            super(v);
            nameTextView = ((TextView)v.findViewById(R.id.nameTextView));
            moreInfoButton = ((Button)v.findViewById(R.id.moreInfoButton));
            showStatsButton = ((Button)v.findViewById(R.id.showStatsButton));
            showDescriptionButton = ((Button)v.findViewById(R.id.showDescriptionButton));
            voteButton = ((Button)v.findViewById(R.id.VoteButton));
        }
    }

    public static class MoreInfoButtonListener implements View.OnClickListener {
        private Law mLaw;
        private LawActivity mLawActivity;

        public MoreInfoButtonListener(Law law, LawActivity lawActivity) {
            mLaw = law;
            mLawActivity = lawActivity;
        }

        @Override
        public void onClick(View v) {
            //the view is the button, we need to get it's parnet
            View parent = (View) v.getParent();
            ExpandableLayout expandableLayout = ((ExpandableLayout) parent.findViewById(R.id.expandable_layout));
            Button b = (Button)v;
            if (expandableLayout.isExpanded()) {
                b.setCompoundDrawablesWithIntrinsicBounds(mLaw.getLawVoteIconDrawableId(), 0, 0, R.drawable.down_arrow2);
            } else {
                b.setCompoundDrawablesWithIntrinsicBounds(mLaw.getLawVoteIconDrawableId(),0, 0, R.drawable.up_arrow2);
            }
            expandableLayout.toggle();
            mLaw.setUserDistAndElectedVotes(mLawActivity);
//            mLaw.DrawVotesGraph(parent,R.id.VoteLikeMe);
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
           if (mLaw.getUserVote() == UserVote.NO_VOTE){
               mPopupMngr.openPopUp(StatisticsPopupMngr.PopUpType.VOTE_FIRST,mLaw );
           }else {
               mPopupMngr.openPopUp(StatisticsPopupMngr.PopUpType.STATS,mLaw );
           }
        }
    }

    public static class VoteButtonListener implements View.OnClickListener {
        private Law mLaw;
        private StatisticsPopupMngr mPopupMngr;

        public VoteButtonListener(Law law,StatisticsPopupMngr statPopupMngr) {
            mLaw = law;
            mPopupMngr = statPopupMngr;
        }

        @Override
        public void onClick(View v) {
            //the view is the button, we need to get it's parnet
            mPopupMngr.openPopUp(StatisticsPopupMngr.PopUpType.VOTE,mLaw );
        }
    }

    public static class ShowDescriptionButtonListener implements View.OnClickListener {
        private Law mLaw;
        private StatisticsPopupMngr mPopupMngr;

        public ShowDescriptionButtonListener(Law law,StatisticsPopupMngr statPopupMngr) {
            mLaw = law;
            mPopupMngr = statPopupMngr;
        }

        @Override
        public void onClick(View v) {
            //the view is the button, we need to get it's parnet
            mPopupMngr.openPopUp(StatisticsPopupMngr.PopUpType.DESCRIPTION,mLaw );
        }
    }

    public StatisticsPopupMngr buildStatsPopupMngr(){
        Context context = this.lawActivity.getApplicationContext();
        NestedScrollView lawPageLayout = this.lawActivity.findViewById(R.id.lawLayout);
        return new StatisticsPopupMngr(context,lawPageLayout, lawActivity);
    }



}

