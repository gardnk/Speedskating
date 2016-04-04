package com.example.gard.speedskating;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DistanceListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> distances;
    private HashMap<String, ArrayList<Skater>> distanceData;

    public DistanceListAdapter(Context context, List<String> distances, HashMap<String, ArrayList<Skater>> distanceData){
        this.context = context;
        this.distances = distances;
        this.distanceData = distanceData;
    }


    @Override
    public int getGroupCount() {
        return distances.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return distanceData.get(distances.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return distanceData.get(distances.get(groupPosition));
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return distanceData.get(distances.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String distanceTitle = distances.get(groupPosition);


        //if(convertView == null){
            //System.out.println("CONVERTVIEW");
            LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, parent, false);
        //}

        TextView header = (TextView)convertView.findViewById(R.id.listHeader);
        header.setText(distanceTitle);

        System.out.println("Group view: "+distanceTitle);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ArrayList<Skater> skaters = distanceData.get(distances.get(groupPosition));
        Skater skater = skaters.get(childPosition);


        LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.list_child_item, parent, false);
        System.out.println("Child view: "+skater.getName());

        TextView name = (TextView)convertView.findViewById(R.id.item);
        name.setText(skater.getName());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
