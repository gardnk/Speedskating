package com.example.gard.speedskating;

import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;

import java.util.ArrayList;

public class RaceData {
    private ConcurrentRadixTree<Skater> skaters;
    private ArrayList<Distance> distances;

    RaceData(ConcurrentRadixTree<Skater> tree, ArrayList<Distance> dist){
        skaters = tree;
        distances = dist;
    }

    public ConcurrentRadixTree<Skater> getSkaters(){ return skaters;}
    public ArrayList<Distance> getDistances(){ return distances;}
}
