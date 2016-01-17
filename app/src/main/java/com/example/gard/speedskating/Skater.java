package com.example.gard.speedskating;

import java.util.ArrayList;
import java.util.List;


public class Skater {
    private String name;
    private ArrayList<Distance> races = new ArrayList<>();
    private ArrayList<Integer> pairs = new ArrayList<>();

    public Skater(String name){
        this.name = name;
    }

    public String getName(){ return name;}
    public void addDistance(Distance d, int pair){ races.add(d); pairs.add(pair);}

    public boolean contains(Distance d){ return races.contains(d);}
    public int getPair(Distance d){ return pairs.get(races.indexOf(d));}
    public List<String> getDistances(){
        List<String> list = new ArrayList<>();
        for(Distance d : races) list.add(d.getDistance());
        return list;
    }
}
