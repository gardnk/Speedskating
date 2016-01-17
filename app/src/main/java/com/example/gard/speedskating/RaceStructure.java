package com.example.gard.speedskating;

import android.content.SharedPreferences;

import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class RaceStructure {
    ArrayList<String> list;
    String urlString;
    SharedPreferences sharedPreferences;

    RaceStructure(String urlString, SharedPreferences preferences){
        list = new ArrayList<>();
        this.urlString = urlString;
        sharedPreferences = preferences;
    }

    public void update(){
        list.clear();
        try {
            URL url = new URL(urlString);
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
    }

    public RaceData getRaceData() {
        ConcurrentRadixTree<Skater> tree = new ConcurrentRadixTree<>(new DefaultCharArrayNodeFactory());
        ArrayList<Distance> distances = new ArrayList<>();
        boolean begun = false;
        Skater skater;
        Distance distance = null;

        // hvis det er en bug i scriptet
        //int skaterNumber = 1;
        int pair;
        //boolean bug = false;

        for (String line : list) {
            String[] data = line.split(";");
            if (!begun) {
                if (data[0].equals("Distanse")) {
                    begun = true;
                }
            } else {
                // er distansen trukket?
                if(data[2].equals("")) continue;

                // hvis det er et par med kun en løper
                if (data.length == 6 || data[6].equals("")) continue;

                String dist = data[0];
                String klasse = data[4];
                String name = data[6];

                // hvis klassene kun er junior/senior, skilles bare menn og kvinner
                //if (klasse.equals("MS") || klasse.equals("MJ")) klasse = "M";
                //else if (klasse.equals("KS") || klasse.equals("KJ")) klasse = "K";

                String distanceString = dist + "" + klasse;
                skater = new Skater(name);

                // set distance and pair
                distance = setDistance(distance, distanceString);
                //if((pair = updatePair(bug,pair,skaterNumber++)) == -1)
                pair = Integer.parseInt(data[2]);
                distance.updatePair(pair);


                // add distance to list of distances to gain control of occurrence
                if (!distances.contains(distance)) distances.add(distance);

                // add skater to auto-complete tree
                tree.putIfAbsent(name.toLowerCase(), skater);
                // add distance and pair to skater
                tree.getValueForExactKey(name.toLowerCase()).addDistance(distance, pair);

                // hvis distansen ikke finnes i innstillinger, legg den til
                addPreference(distanceString);
            }
        }
        return new RaceData(tree,distances);
    }

    public Distance setDistance(Distance distance, String distanceString){
        if (distance == null || !distance.getDistance().equals(distanceString))
            return new Distance(distanceString);
        return distance;
    }

    public void addPreference(String key){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(sharedPreferences.contains(key)) return;
        editor.putInt(key, 1);
        editor.commit();
    }

    public int getPairTime(String distance){
        return 0;
    }
}
