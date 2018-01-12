package com.example.inbar.heimdall.Law;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Eilon on 05/01/2018.
 */

public class Law {
    public String name;
    public VoteStatus voteStat;
    public String description;
    public Drawable image;
    public View.OnClickListener forButtonListener;

    public Law(){}

    public Law(String name, VoteStatus status, String description, Drawable image) {
        this.name = name;
        this.voteStat = status;
        this.description = description;
        this.image = image;
        this.forButtonListener = new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Law PRESSED = " + , Toast.LENGTH_SHORT).show();
            }
        };
    }

}

