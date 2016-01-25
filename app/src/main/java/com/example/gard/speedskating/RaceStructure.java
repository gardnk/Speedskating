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
    TimeData timeData;

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

                // set distance
                String distanceString = dist + "" + klasse;
                distance = setDistance(distance, distanceString);

                // create/update skater
                skater = updateSkater(name, tree, distance, data);

                // update number of pairs in distance
                pair = Integer.parseInt(data[2]);
                distance.updatePair(pair);

                if(getProgress(data, 8) == null) {
                    timeData = new TimeData(System.currentTimeMillis(),distance);
                    distance.setLivePair(pair);
                }

                // add distance to list of distances to gain control of occurrence
                if (!distances.contains(distance)) distances.add(distance);

                // add skater to auto-complete tree
                tree.putIfAbsent(name.toLowerCase(), skater);
                // add distance and pair to skater
                tree.getValueForExactKey(name.toLowerCase()).addDistance(distance, pair);

                // add default preference value if no value exists already
                if(!sharedPreferences.contains(distance.getDistance())) addDefaultPrefs(distance);
            }
        }
        // if the last distance is finished
        // works only when distance is live
        if(distance != null && distance.getPairs() == distance.getLivePair()) distance.setFinished();
        return new RaceData(tree,distances);
    }

    public String getProgress(String[] data, int index){
        // hvis det ikke er flere passeringstider, altså paret er ferdig
        if(data.length <= index) return "End";
        // hvis data er null, er det så langt løpet har kommet
        if(data[index] == null) return null;
        else{
            // kjør rekursivt gjennom data
            return getProgress(data,++index);
        }
    }

    public Distance setDistance(Distance distance, String distanceString){
        if (distance == null || !distance.getDistance().equals(distanceString)) {
            if(distance != null) distance.setFinished();
            return new Distance(distanceString);
        }
        return distance;
    }

    public TimeData getTimeData(){ return timeData;}

    public Skater updateSkater(String name, ConcurrentRadixTree<Skater> tree, Distance d, String[] data){
        Skater s = tree.getValueForExactKey(name.toLowerCase());
        if(s != null){
           String time = data[8];
            long currentTime = System.currentTimeMillis();
            //MainActivityFragment.currentTime = currentTime;
            if(time != null && !s.timeSet(d)) s.addTime(currentTime);
           return s;
       } else {
           return new Skater(name);
       }
    }

    public void addDefaultPrefs(Distance d){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String distance = d.getDistance();

        if(distance.contains("500")){
            editor.putLong(distance,1);
        } else if(distance.contains("1000")){
            editor.putLong(distance,2);
        } else if(distance.contains("1500")){
            editor.putLong(distance,3);
        } else if(distance.contains("3000")){
            editor.putLong(distance,5);
        } else if(distance.contains("5000")){
            editor.putLong(distance,8);
        } else if(distance.contains("10000")){
            editor.putLong(distance, 16);
        } else {
            editor.putLong(distance, 1);
        }

        editor.apply();
    }
}
