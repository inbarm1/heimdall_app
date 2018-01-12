package com.example.inbar.heimdall.Law;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inbar.heimdall.R;

import java.util.List;

/**
 * Created by Eilon on 05/01/2018.
 */

public class LawListAdapter extends RecyclerView.Adapter<LawListAdapter.SimpleViewHolder> {
    private Law[]  mLaws;
    protected LawActivity lawActivity;

    public LawListAdapter(Law[] laws, LawActivity father) {
        mLaws = laws;
        lawActivity = father;

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
        final Law law = mLaws[position];
        holder.nameTextView.setText(law.getName());
        holder.roleTextView.setText(law.getDescription());
        holder.moreInfoButton.setOnClickListener(new VoteButtonListener(law));
    }

    @Override
    public int getItemCount() {
        return mLaws.length;
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {

        protected TextView nameTextView;
        protected TextView roleTextView;
        protected Button moreInfoButton;

        public SimpleViewHolder(View v) {
            super(v);
            nameTextView = ((TextView)v.findViewById(R.id.nameTextView));
            roleTextView = ((TextView)v.findViewById(R.id.roleTextView));
            moreInfoButton = ((Button)v.findViewById(R.id.moreInfoButton));
        }
    }

    public class VoteButtonListener implements View.OnClickListener {
        private Law mLaw;

        public VoteButtonListener(Law law){
            mLaw = law;
        }
        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), "Law PRESSED = " +mLaw.getName() , Toast.LENGTH_SHORT).show();
            if (mLaw.getVoteStat() == VoteStatus.NO_VOTE) {


            }else{

            }
        }
    }


}

