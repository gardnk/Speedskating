package com.example.gard.speedskating;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


class GetStartlist extends AsyncTask<Void, Void, Void> {
    List<String> list;
    ArrayList<Distance> distances;
    ConcurrentRadixTree<Skater> tree;
    SharedPreferences preferences;

    GetStartlist(SharedPreferences preferences){
        this.preferences = preferences;
        list = new ArrayList<String>();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            URL url = new URL("http://web.glitretid.no/csv.php?default");
            URLConnection connection = url.openConnection();
            connection.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = in.readLine()) != null) {
                list.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        createStructure();
        MainActivityFragment.tree = tree;
        MainActivityFragment.distances = distances;
    }

    void createStructure() {
        tree = new ConcurrentRadixTree<>(new DefaultCharArrayNodeFactory());
        distances = new ArrayList<>();
        boolean begun = false;
        Skater skater;
        Distance distance = null;

        // hvis det er en bug i scriptet
        int pairBug = 1;
        int skaterNumber = 1;
        boolean bug = false;

        for (String line : list) {
            String[] data = line.split(";");
            if (!begun) {
                if (data[0].equals("Distanse")) {
                    begun = true;
                }
            } else {
                if (data[6].equals("")){
                    if(bug) pairBug = setPairNumber(pairBug, skaterNumber++);
                    continue; // hvis det ikke er noen løper (altså et par har én løper)
                }

                String dist = data[0];
                int pair;
                String klasse = data[4];
                String name = data[6];

                // hvis løperne ikke har parnummer (bug i scriptet)
                if(data[2].equals("")) {
                    bug = true;
                    pairBug = setPairNumber(pairBug, skaterNumber++);
                    pair = pairBug;
                }
                else pair = Integer.parseInt(data[2]);

                // hvis klassene kun er junior/senior, skilles bare menn og kvinner
                if (klasse.equals("MS") || klasse.equals("MJ")) klasse = "M";
                else if (klasse.equals("KS") || klasse.equals("KJ")) klasse = "K";

                String distanceString = dist + "" + klasse;
                skater = new Skater(name);

                if (distance == null || !distance.getDistance().equals(distanceString)) {
                    distance = new Distance(distanceString);
                } else {
                    distance.updatePair(pair);
                }

                // add distance to list of distances to gain control of occurrence
                if (!distances.contains(distance)) distances.add(distance);

                // add skater to auto-complete tree
                tree.putIfAbsent(name.toLowerCase(), skater);
                // add distance and pair to skater
                tree.getValueForExactKey(name.toLowerCase()).addDistance(distance, pair);

                // save preferences
                savePreferences(klasse);
            }
        }
    }

    void savePreferences(String klasse){
        SharedPreferences.Editor editor = preferences.edit();
        if(!preferences.contains(klasse)){
            editor.putFloat(klasse, 2);
            editor.apply();
        }
    }

    public int setPairNumber(int pair, int skaterNumber){
        int fullPair = skaterNumber % 2;
        if(fullPair == 0){
            return pair+1;
        } else return pair;
    }
}
