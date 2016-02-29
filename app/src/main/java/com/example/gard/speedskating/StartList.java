package com.example.gard.speedskating;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class StartList implements Parcelable {

    private List startList;

    public StartList(){ startList = new LinkedList<>();}

    public void addDistance(Distanse distance){
        if(!startList.contains(distance))
            startList.add(distance);
    }

    public boolean removeSkater(Skater toRemove) {
        return startList.remove(toRemove);
    }

    public Skater removeSkater(int distanceIndex, int pairIndex){
        Distanse dist = (Distanse)startList.get(distanceIndex);
        ArrayList<Skater> skaters = dist.getSkaters();
        return skaters.remove(pairIndex);
    }

    public Iterator<Distanse> getStartList() {
        return startList.iterator();
    }

    public int getListLength(){ return startList.size();}

    public boolean contains(String distanse){
        Iterator<Distanse> starListIterator = getStartList();
        while(starListIterator.hasNext()){
            if(starListIterator.next().getDistance().equals(distanse)) return true;
        }
        return false;
    }

    // returnerer indexen til løperen i startlista
    // -1 hvis løperen ikke finnes på startlista
    public int indexOf(Skater skater){
        String name = skater.getName();
        Iterator<Distanse> distanseIterator = getStartList();
        while (distanseIterator.hasNext()){
            int index = 0;
            for (Skater s : distanseIterator.next().getSkaters()){
                if(s.getName().equals(name)) return index;
                index++;
            }
        }
        return -1;
    }

    // Parcelable part

    public StartList(Parcel in){
        //LinkedList data = new LinkedList();
        List data = new LinkedList<>();
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        in.readList(data, classLoader);
        this.startList = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.startList);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public StartList createFromParcel(Parcel in){
            return new StartList(in);
        }

        public StartList[] newArray(int size){
            return new StartList[size];
        }
    };
}
