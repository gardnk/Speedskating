package com.example.gard.speedskating;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivityFragment extends android.support.v4.app.Fragment {
    final static int BOLD = 1;
    //final static int NORMAL = 0;

    SharedPreferences preferences;
    public static ArrayList<Distance> distances;
    public static ConcurrentRadixTree<Skater> tree;
    public TimeData timeData;

    View main;
    //public FloatingActionButton searchButton;
    public Button searchButton;
    public EditText searchField;
    public ListView skaterList;
    ViewPager pager;
    SkaterTabAdapter adapter;
    PagerSlidingTabStrip tabStrip;
    Typeface typeface;
    public boolean tabSet = false;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        main = inflater.inflate(R.layout.fragment_activity, container, false);
        typeface = Typeface.createFromAsset(getActivity().getAssets(),"Neon.ttf");
        changeStatusBarColor();
        updateViews();
        startListeners();
        mainViewListener();
        return main;
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState){
        super.onViewCreated(v, savedInstanceState);
        getUpdatingStartlist();
    }

    public void changeStatusBarColor(){
        Window window = getActivity().getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // set color to the status bar
        window.setStatusBarColor(getColor(getContext(),R.color.statusbar));
    }

    // get color resource depending on api level
    public int getColor(Context context, int id){
        // get color method only works in api level 23 and above
        if(Build.VERSION.SDK_INT >= 23){
            return context.getResources().getColor(id, null);
        } else {
            return ContextCompat.getColor(context,id);
        }
    }

    void updateViews(){
        //searchButton = (FloatingActionButton)main.findViewById(R.id.search_button);
        searchButton = (Button)main.findViewById(R.id.search_button2);
        searchField = (EditText)main.findViewById(R.id.search_field);
        skaterList = (ListView)main.findViewById(R.id.skater_list);
        pager = (ViewPager)main.findViewById(R.id.pager);
        tabStrip = (PagerSlidingTabStrip)main.findViewById(R.id.tab_strip);

        searchField.setVisibility(View.INVISIBLE);
        skaterList.setVisibility(View.INVISIBLE);
        pager.setVisibility(View.INVISIBLE);
        tabStrip.setVisibility(View.INVISIBLE);

        searchField.setTypeface(typeface);
    }

    void mainViewListener(){
        main.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                skaterList.setVisibility(View.INVISIBLE);
                hideSoftKeyboard();
                pager.setVisibility(View.VISIBLE);
                tabStrip.setVisibility(View.VISIBLE);
                searchField.setVisibility(View.INVISIBLE);
                main.requestFocus();
                return true;
            }
        });
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
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),R.layout.list_item){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                if(convertView == null){
                    ViewGroup root = (ViewGroup)main.findViewById(android.R.id.content);
                    LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.list_item,root);
                    TextView textView = (TextView)convertView.findViewById(R.id.text1);
                    textView.setTypeface(typeface);
                }
                return super.getView(position, convertView, parent);
            }
        };
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.clear();
                for (Skater skater : tree.getValuesForClosestKeys(s)) {
                    adapter.add(skater.getName());
                }
                skaterList.setAdapter(adapter);
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
        tabSet = true;
        // calculate tab info (times and distances)
        ArrayList<Integer> times = getTimes(skater);
        ArrayList<String> titles = getTabTitles(skater);

        adapter = new SkaterTabAdapter(getFragmentManager(), skater, times, titles);
        pager.setAdapter(adapter);
        tabStrip.setViewPager(pager);
        tabStrip.setIndicatorColor(R.color.statusbar);
        tabStrip.setTextColor(R.color.statusbar);
        tabStrip.setDividerColor(R.color.statusbar);
        tabStrip.setTypeface(typeface, BOLD);

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
        // if all races are finished
        if(temp.isEmpty()) temp.add("No races");
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
                Long remainingPairs = getRemainingPairs(skater, d, pair);
                Long tmpTime = time+(remainingPairs*pairTime);
                // add closest integer to the long
                tmpTimes.add(tmpTime.intValue());
            }
            // add the remaining pairs
            time += (d.getPairs()-pair)*pairTime;
        }
        // if all races are finished
        if(tmpTimes.isEmpty()) tmpTimes.add(0);
        return tmpTimes;
    }

    public Long getRemainingPairs(Skater skater, Distance d, Long currentPair){
        return (long)skater.getPair(d)-currentPair;
    }

    private class NetworkActivity extends AsyncTask<Void,Void,TimeData> {

        RaceStructure raceStructure;
        Activity mainActivity;
        LoadingDialog loading;

        public NetworkActivity(Activity a){
            mainActivity = a;
            loading = new LoadingDialog(mainActivity, R.style.loading_dialog);
            loading.setContentView(R.layout.loading);
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
