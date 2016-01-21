package com.example.gard.speedskating;


public class TimeData {
    private long time;
    private Distance distance;
    private TimeData next;

    TimeData(long time, Distance distance){
        this.time = time;
        this.distance = distance;
        next = null;
    }

    public Distance getDistance(){ return distance;}
    public long getTime(){ return time;}

    public void setNext(TimeData data){
        next = data;
    }
}
