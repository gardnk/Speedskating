package com.example.gard.speedskating;


public class Pair {
    private boolean finished = false;
    private long time;

    public void setTime(long time){ this.time = time;}
    public void setFinished(){ finished = true;}

    public boolean isFinished(){ return finished;}
}
