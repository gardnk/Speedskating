package com.example.gard.speedskating;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;


public class Distanse implements Parcelable{
    private String distance;
    private ArrayList<Skater> pairs = new ArrayList<>();

    public Distanse(String dist){
        this.distance = dist;
    }

    public void addSkater(Skater skater){
        pairs.add(skater);
    }

    public ArrayList<Skater> getSkaters(){
        return pairs;
    }

    public String getDistance(){ return distance;}

    /**
     * Parcelable part of the class
     *
     * For passing object using bundle
     */


    public Distanse(Parcel in){
        String[] data = new String[1];
        in.readStringArray(data);

        this.distance = data[0];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.distance});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public Distanse createFromParcel(Parcel in){
            return new Distanse(in);
        }

        public Distanse[] newArray(int size){
            return new Distanse[size];
        }
    };
}
