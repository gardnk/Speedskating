package com.example.gard.speedskating;


public class TimeData {
    private long time;
    private Distance distance;
    private int pair;

    TimeData(long time, Distance distance, int pair){
        this.time = time;
        this.distance = distance;
        this.pair = pair;
    }

    public Distance getDistance(){ return distance;}
    public long getTime(){ return time;}
    public int getPair(){ return pair;}
}
