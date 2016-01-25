package com.example.gard.speedskating;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class SkaterFragment extends Fragment {
    private int time;
    private int breaks;
    public TextView timeView;
    private FloatingActionButton breakButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.skater_tab, container, false);
        String name = getArguments().getString("name");
        time = getArguments().getInt("time");
        breaks = 0;

        // set text view with name
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(),"Neon.ttf");
        AutoResizeTextView textView = (AutoResizeTextView)v.findViewById(R.id.text);
        textView.setTypeface(typeface);
        textView.setTextSize(150);
        textView.setText(name);
        textView.resizeText();

        // set text view with time
        timeView = (TextView)v.findViewById(R.id.time_view);
        updateViews();

        // add button for adding breaks
        breakButton = (FloatingActionButton)v.findViewById(R.id.breaks);
        breakButton.setBackground(getActivity().getDrawable(R.drawable.zamboni));
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
