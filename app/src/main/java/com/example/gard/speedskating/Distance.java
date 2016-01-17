package com.example.gard.speedskating;

/**
 * Created by gard on 13.01.16.
 */
public class Distance {
    private String dist;
    private int pairs;

    public Distance(String dist){ this.dist = dist; pairs = 0;}

    public void updatePair(int pair){ pairs = pair;}

    public String getDistance(){ return dist;}
    public int getPairs(){ return pairs;}
}
