package com.example.gard.speedskating;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.EditText;

import java.sql.Time;

public class NetworkActivity extends AsyncTask<Void,Void,TimeData> {

    RaceStructure raceStructure;
    Activity mainActivity;
    SharedPreferences preferences;
    MainActivityFragment fragment;

    public NetworkActivity(Activity a, SharedPreferences preferences, MainActivityFragment fragment){
        mainActivity = a;
        this.preferences = preferences;
        this.fragment = fragment;
    }

    @Override
    protected TimeData doInBackground(Void... params) {
        raceStructure = new RaceStructure("http://web.glitretid.no/csv.php?default", preferences);
        raceStructure.update();
        return null;
    }

    @Override
    protected void onPostExecute(TimeData data){
        RaceData raceData = raceStructure.getRaceData();
        MainActivityFragment.tree = raceData.getSkaters();
        MainActivityFragment.distances = raceData.getDistances();
        //mainActivity.setContentView(R.layout.fragment_activity);
        fragment.updateViews();
        fragment.startListeners();
        //EditText editText = (EditText)mainActivity.findViewById(R.id.search_field);
    }
}
