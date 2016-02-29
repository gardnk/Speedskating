package com.example.gard.speedskating;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class StartListActivity extends Fragment {

    private StartList startList;
    public ListView list;
    public ArrayAdapter<String> listAdapter;

    // expandable
    public ExpandableListView listView;
    public ExpandableListAdapter adapter;

    private ArrayList<String> headers;
    private HashMap<String, ArrayList<Skater>> listChildren;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();

        if(bundle != null) {
            startList = bundle.getParcelable("startlist");
            createListItems();
        } else {
            Toast.makeText(getContext(),"Fant ingen startliste.", Toast.LENGTH_LONG).show();
        }
    }

    public void createListItems(){
        headers = new ArrayList<>();
        listChildren = new HashMap<>();
        Iterator<Distanse> iterator = startList.getStartList();

        while(iterator.hasNext()){
            Distanse d = iterator.next();
            headers.add(d.getDistance());
            listChildren.put(d.getDistance(), d.getSkaters());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v =  inflater.inflate(R.layout.startlist_fragment, container, false);
        list = (ListView)v.findViewById(R.id.startlist);
        listView = (ExpandableListView)v.findViewById(R.id.expandable_list);
        setList();
        startSwipeListener();
        return v;
    }

    public void setList(){
        //listAdapter = new ArrayAdapter<>(getContext(),R.layout.list_item);
        adapter = getExpandableAdapter(headers, listChildren);
        adapter.getGroupView(0, false, list, listView);

        listView.setAdapter(adapter);
    }

    public DistanceListAdapter getExpandableAdapter(List<String> headers, HashMap<String, ArrayList<Skater>> listChildData){
        return new DistanceListAdapter(getContext(), headers, listChildData);
    }

    public void startSwipeListener(){
        final SwipeDetector swipeDetector = new SwipeDetector();
        listView.setOnTouchListener(swipeDetector);

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if(swipeDetector.swipeDetected()){
                    SwipeDetector.Action swipe = swipeDetector.getAction();
                    if(swipe == SwipeDetector.Action.LR){

                        // get the skater that was swiped
                        Skater skater = (Skater)adapter.getChild(groupPosition, childPosition);

                        // remove skater from startlist
                        int index = startList.indexOf(skater);
                        if(index != -1) {
                            startList.removeSkater(groupPosition, childPosition);
                        }

                        // reload listview
                        createListItems();
                        setList();
                    }
                }
                return true;
            }
        });
    }
}
