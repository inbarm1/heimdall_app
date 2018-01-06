package com.example.inbar.heimdall.Law;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.inbar.heimdall.R;

import java.util.List;

/**
 * Created by Eilon on 05/01/2018.
 */

public class LawListAdapter extends RecyclerView.Adapter<LawListAdapter.SimpleViewHolder> {
    private List<Law> mLaws;

    public LawListAdapter(List<Law> laws) {
        mLaws = laws;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_law_layout, parent, false);
        SimpleViewHolder holder = new SimpleViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        final Law law = mLaws.get(position);
        holder.nameTextView.setText(law.getName());
        holder.roleTextView.setText(law.getRole());
    }

    @Override
    public int getItemCount() {
        return mLaws.size();
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {

        protected TextView nameTextView;
        protected TextView roleTextView;

        public SimpleViewHolder(View v) {
            super(v);
            nameTextView = ((TextView)v.findViewById(R.id.nameTextView));
            roleTextView = ((TextView)v.findViewById(R.id.roleTextView));
        }
    }
}

