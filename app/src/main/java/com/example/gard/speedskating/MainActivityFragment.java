package com.example.gard.speedskating;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.schedulers.HandlerScheduler;
import rx.schedulers.Schedulers;
import static android.os.Process.THREAD_PRIORITY_BACKGROUND;


public class MainActivityFragment extends android.support.v4.app.Fragment {
    SharedPreferences preferences;
    public static ArrayList<Distance> distances;
    public static ConcurrentRadixTree<Skater> tree;

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
        // start network activity
        //getUpdatingStartlist();
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
        //startListeners();
        //getUpdatingStartlist();
        callAsynchronousTask();
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


    // for å kjøre async task flere ganger
    public void getUpdatingStartlist() {
        // instantiate preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        NetworkActivity activity = new NetworkActivity(getActivity());
        activity.execute();
    }

    public void searchListener(){
        System.out.println("HORE");
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("FITTE");
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
        // calculate tab info (times)
        ArrayList<Integer> times = getTimes(skater);

        adapter = new SkaterTabAdapter(getFragmentManager(), skater, times);
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

    ArrayList<Integer> getTimes(Skater skater){
        ArrayList<Integer> tmpTimes = new ArrayList<>();
        int time = 0;
        for(Distance d : distances){
            int pairTime = preferences.getInt(d.getDistance(),5);
            if(skater.contains(d)){
                time += skater.getPair(d)*pairTime;
                tmpTimes.add(time);
            }
            time += d.getPairs()*pairTime;
        }
        return tmpTimes;
    }

    private class NetworkActivity extends AsyncTask<Void,Void,Void> {

        RaceStructure raceStructure;
        Activity mainActivity;
        ProgressDialog progressDialog;

        public NetworkActivity(Activity a){
            mainActivity = a;
        }

        @Override
        public void onPreExecute(){
            super.onPreExecute();
            //mainActivity.setContentView(R.layout.loading);
            progressDialog = ProgressDialog.show(mainActivity,"Loading","loading csv",true);
            //getActivity().getSupportFragmentManager().beginTransaction().add(R.id.loading, new LoadingFragment()).commit();
        }

        @Override
        protected Void doInBackground(Void... params) {
            raceStructure = new RaceStructure("http://web.glitretid.no/csv.php?default", preferences);
            raceStructure.update();
            return null;
        }

        @Override
        protected void onPostExecute(Void v){
            RaceData raceData = raceStructure.getRaceData();
            tree = raceData.getSkaters();
            distances = raceData.getDistances();
            progressDialog.dismiss();
            //System.out.println("End");
            //updateViews();
            //startListeners();
        }
    }


    public void callAsynchronousTask() {
        // start by running network activity once
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
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
                            new NetworkActivity(getActivity()).execute();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 20000); //execute in every 20 seconds
    }
}
