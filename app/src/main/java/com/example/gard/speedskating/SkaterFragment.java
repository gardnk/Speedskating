package com.example.gard.speedskating;

import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class SkaterFragment extends Fragment {
    private int time;
    private int breaks;
    public TextView timeView;
    private FloatingActionButton addBreaks;
    private FloatingActionButton subtractBreaks;
    public TextView breakNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.skater_tab, container, false);
        String name = getArguments().getString("name");
        time = getArguments().getInt("time");
        breaks = 0;
        String breakDisplay = ""+breaks;

        // set text view with name
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(),"Neon.ttf");
        AutoResizeTextView textView = (AutoResizeTextView)v.findViewById(R.id.text);
        textView.setTypeface(typeface);
        textView.setTextSize(150);
        textView.setText(name);
        textView.resizeText();

        // set text view with time and break view with breaks
        timeView = (TextView)v.findViewById(R.id.time_view);
        timeView.setTypeface(typeface);
        breakNumber = (TextView)v.findViewById(R.id.break_number_display);
        breakNumber.setTypeface(typeface);
        breakNumber.setText(breakDisplay);
        updateViews();

        // add buttons for managing breaks
        addBreaks = (FloatingActionButton)v.findViewById(R.id.add_breaks);
        addBreaks.setBackgroundTintList(getColorStateList(R.color.addBreak));
        subtractBreaks = (FloatingActionButton)v.findViewById(R.id.subtract_breaks);
        subtractBreaks.setBackgroundTintList(getColorStateList(R.color.subBreak));

        breakListener();

        return v;
    }

    public ColorStateList getColorStateList(int id){
        if(Build.VERSION.SDK_INT >= 23){
            return getResources().getColorStateList(id,null);
        } else {
            return ContextCompat.getColorStateList(getContext(),id);
        }
    }

    public void breakListener(){
        addBreaks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                breaks++;
                updateViews();
            }
        });

        subtractBreaks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                breaks--;
                updateViews();
            }
        });
    }

    public void updateViews(){
        String time = getTime();
        String breakDisplay = ""+breaks;
        timeView.setText(time);
        breakNumber.setText(breakDisplay);
    }

    private String getTime(){
        int totalTime = time+(20*breaks);
        int minute = totalTime%60;
        int hour = totalTime/60;
        if(minute < 10) return hour+".0"+minute;
        return hour+"."+minute;
    }
}
