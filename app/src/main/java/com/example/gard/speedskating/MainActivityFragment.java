package com.example.gard.speedskating;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivityFragment extends android.support.v4.app.Fragment {
    SharedPreferences preferences;
    public static ArrayList<Distance> distances;
    public static ConcurrentRadixTree<Skater> tree;
    public TimeData timeData;

    View main;
    public FloatingActionButton searchButton;
    public EditText searchField;
    public ListView skaterList;
    ViewPager pager;
    SkaterTabAdapter adapter;
    PagerSlidingTabStrip tabStrip;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        main = inflater.inflate(R.layout.fragment_activity, container, false);
        updateViews();
        startListeners();
        return main;
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState){
        super.onViewCreated(v, savedInstanceState);
        getUpdatingStartlist();
    }

    void updateViews(){
        //getActivity().setContentView(R.layout.fragment_activity);
        searchButton = (FloatingActionButton)main.findViewById(R.id.search_button);
        searchField = (EditText)main.findViewById(R.id.search_field);
        skaterList = (ListView)main.findViewById(R.id.skater_list);
        pager = (ViewPager)main.findViewById(R.id.pager);
        tabStrip = (PagerSlidingTabStrip)main.findViewById(R.id.tab_strip);

        searchField.setVisibility(View.INVISIBLE);
        skaterList.setVisibility(View.INVISIBLE);
        pager.setVisibility(View.INVISIBLE);
        tabStrip.setVisibility(View.INVISIBLE);
    }

    public void startListeners(){
        searchListener();
        textListener();
        listWatcher();
    }

    void hideSoftKeyboard(){
        Activity a = getActivity();
        if(a.getCurrentFocus() == null) return;
        InputMethodManager imm = (InputMethodManager)a.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(a.getCurrentFocus().getWindowToken(), 0);
    }

    void showSoftKeyboard(){
        Activity a = getActivity();
        if(searchField.requestFocus() && a.getCurrentFocus() != null){
            InputMethodManager imm = (InputMethodManager)a.getSystemService(Context.INPUT_METHOD_SERVICE);
            //imm.showSoftInputFromInputMethod(a.getCurrentFocus().getWindowToken(),0);
            imm.toggleSoftInputFromWindow(a.getCurrentFocus().getWindowToken(),0,0);
        }
    }

    public void searchListener(){
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchField.setVisibility(View.VISIBLE);
                skaterList.setVisibility(View.VISIBLE);
                showSoftKeyboard();
                hideTabs();
            }
        });
    }

    public void textListener(){
        final ArrayAdapter<String> listAdapter = new ArrayAdapter<>(getContext(), R.layout.list_item);
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listAdapter.clear();
                for (Skater skater : tree.getValuesForClosestKeys(s)) {
                    listAdapter.add(skater.getName());
                }
                skaterList.setAdapter(listAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void listWatcher(){
        skaterList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hideSoftKeyboard();
                skaterList.setVisibility(View.INVISIBLE);
                searchField.setVisibility(View.INVISIBLE);
                String name = (String) parent.getItemAtPosition(position);
                Skater skater = tree.getValueForExactKey(name.toLowerCase());
                showTabs(skater);
            }
        });
    }

    public void showTabs(Skater skater){
        // calculate tab info (times and distances)
        ArrayList<Integer> times = getTimes(skater);
        ArrayList<String> titles = getTabTitles(skater);

        adapter = new SkaterTabAdapter(getFragmentManager(), skater, times, titles);
        pager.setAdapter(adapter);
        tabStrip.setViewPager(pager);
        tabStrip.setIndicatorColor(R.color.main);
        tabStrip.setTextColor(R.color.main);
        tabStrip.setDividerColor(R.color.transparent);

        // make the tabs visible
        pager.setVisibility(View.VISIBLE);
        tabStrip.setVisibility(View.VISIBLE);
    }

    void hideTabs(){
        if(pager != null && tabStrip != null) {
            pager.setVisibility(View.INVISIBLE);
            tabStrip.setVisibility(View.INVISIBLE);
        }
    }

    ArrayList<String> getTabTitles(Skater skater){
        ArrayList<String> temp = new ArrayList<>();
        for(Distance d : distances){
            if(d.isFinished()) continue;
            if(skater.contains(d)) {
                if (d.getLivePair() > skater.getPair(d)) continue;
                temp.add(d.getDistance());
            }
        }
        return temp;
    }

    ArrayList<Integer> getTimes(Skater skater){
        ArrayList<Integer> tmpTimes = new ArrayList<>();
        Long time = (long)0;

        for(Distance d : distances){
            // if the distance is finished
            if(d.isFinished()) continue;

            // get the pair currently skating
            Long pair = (long)d.getLivePair();
            Long pairTime = preferences.getLong(d.getDistance(), -1);

            if(skater.contains(d)){
                Long remainingPairs = getRemaininPairs(skater,d,pair);
                Long tmpTime = time+(remainingPairs*pairTime);
                // add closest integer to the long
                tmpTimes.add(tmpTime.intValue());
            }
            // add the remaining pairs
            time += (d.getPairs()-pair)*pairTime;
        }
        return tmpTimes;
    }

    public Long getRemaininPairs(Skater skater, Distance d, Long currentPair){
        return (long)skater.getPair(d)-currentPair;
    }

    private class NetworkActivity extends AsyncTask<Void,Void,TimeData> {

        RaceStructure raceStructure;
        Activity mainActivity;
        LoadingDialog loading;

        public NetworkActivity(Activity a){
            mainActivity = a;
            loading = new LoadingDialog(mainActivity);
        }

        @Override
        public void onPreExecute(){
            super.onPreExecute();
            loading.show();
        }

        @Override
        protected TimeData doInBackground(Void... params) {
            raceStructure = new RaceStructure("http://web.glitretid.no/csv.php?default", preferences);
            raceStructure.update();
            return raceStructure.getTimeData();
        }

        @Override
        protected void onPostExecute(TimeData data){
            RaceData raceData = raceStructure.getRaceData();
            updatePreferences(data, timeData);
            tree = raceData.getSkaters();
            distances = raceData.getDistances();
            loading.dismiss();
        }
    }


    public void getUpdatingStartlist() {
        // start by running network activity once
        new NetworkActivity(getActivity()).execute();

        // then run it every 20 seconds to update
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            // handle time settings here
                            // få execute til å returnere et objekt med tidsdata (tid, distanse)
                            new NetworkActivity(getActivity()).execute();
                        } catch (Exception e) {
                            Toast.makeText(getContext(),"Couldn't conenct to GlitreTid.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 20000); //execute in every 20 seconds
    }

    public void updatePreferences(TimeData current, TimeData previous){
        // hvis det er første distanse
        if(timeData == null){
            timeData = current;
            return;
        }

        Distance prev = previous.getDistance();
        Distance curr = current.getDistance();

        // hvis distansen ikke er ferdig enda
        if(prev.getDistance().equals(curr.getDistance())) return;

        // seconds
        Long start = previous.getTime()/1000;
        Long end = current.getTime()/1000;

        Long averageTime = (end-start)/prev.getPairs();

        // update preferences
        addPreferences(averageTime,prev);

        // update previous distance
        timeData = current;
    }

    public void addPreferences(Long time, Distance d){
        SharedPreferences.Editor editor = preferences.edit();
        Long currentPref;

        // hvis distansen finnes i innstillinger fra før
        if((currentPref = preferences.getLong(d.getDistance(), -1)) != -1){
            Long nextPref = (currentPref+time)/2;
            editor.putLong(d.getDistance(), nextPref);
        } else {
            editor.putLong(d.getDistance(), time);
        }
        // lagre endringer
        editor.apply();
    }
}
