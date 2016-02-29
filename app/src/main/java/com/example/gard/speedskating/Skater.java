package com.example.gard.speedskating;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;


public class Skater implements Parcelable {
    private String name;
    private ArrayList<Distance> races = new ArrayList<>();
    private ArrayList<Integer> pairs = new ArrayList<>();
    private ArrayList<Long> previousTimes = new ArrayList<>();

    public Skater(String name){
        this.name = name;
    }

    public String getName(){ return name;}
    public void addDistance(Distance d, int pair){ races.add(d); pairs.add(pair);}
    public void addTime(Long timeFinished){ previousTimes.add(timeFinished);}

    public boolean contains(Distance d){return races.contains(d);}
    public int getPair(Distance d){ return pairs.get(races.indexOf(d));}
    public List<String> getDistances(){
        List<String> list = new ArrayList<>();
        for(Distance d : races) list.add(d.getDistance());
        return list;
    }
    public boolean timeSet(Distance d){
        if(previousTimes.size() >= races.indexOf(d)) return false;
        else return true;
    }


    /**
     *  Parcelable part of the class
     *  for passiing object using bundle
     *
     */

    public Skater(Parcel in){
        String[] data = new String[1];
        in.readStringArray(data);

        this.name = data[0];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] { this.name});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public Skater createFromParcel(Parcel in){
            return new Skater(in);
        }

        public Skater[] newArray(int size){
            return new Skater[size];
        }
    };
}
