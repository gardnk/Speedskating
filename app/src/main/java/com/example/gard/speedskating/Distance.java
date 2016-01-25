package com.example.gard.speedskating;


public class Distance {
    private String dist;
    private int pairs;
    private boolean finished;
    private int livePair;

    public Distance(String dist){
        this.dist = dist;
        pairs = 0;
        finished = false;
        livePair = 0;
    }

    public void updatePair(int pair){ pairs = pair;}
    public void setFinished(){ finished = true;}
    public void setLivePair(int pair){ livePair = pair;}

    public String getDistance(){ return dist;}
    public int getPairs(){ return pairs;}
    public boolean isFinished(){ return finished;}
    public int getLivePair(){ return livePair;}
}
