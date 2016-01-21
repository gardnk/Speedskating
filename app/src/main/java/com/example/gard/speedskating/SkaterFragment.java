package com.example.gard.speedskating;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class SkaterFragment extends Fragment {
    private String name;
    private int time;
    private int breaks;
    public TextView textView;
    public TextView timeView;
    private FloatingActionButton breakButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.skater_tab, container, false);
        name = getArguments().getString("name");
        time = getArguments().getInt("time");
        breaks = 0;

        textView = (TextView) v.findViewById(R.id.text);
        textView.setText(name);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(),"REZ.ttf");
        textView.setTypeface(typeface);

        timeView = (TextView)v.findViewById(R.id.time_view);
        updateViews();

        breakButton = (FloatingActionButton)v.findViewById(R.id.breaks);
        breakListener();

        return v;
    }

    public void breakListener(){
        breakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                breaks++;
                updateViews();
            }
        });

        breakButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                breaks--;
                updateViews();
                return true;
            }
        });
    }

    public void updateViews(){
        String time = getTime();
        timeView.setText(time);
    }

    private String getTime(){
        int totalTime = time+(20*breaks);
        int minute = totalTime%60;
        int hour = totalTime/60;
        if(minute < 10) return hour+".0"+minute;
        return hour+"."+minute;
    }
}
