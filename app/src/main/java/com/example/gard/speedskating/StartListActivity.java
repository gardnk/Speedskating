package com.example.gard.speedskating;

import android.content.ClipData;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.terlici.dragndroplist.DragNDropAdapter;
import com.terlici.dragndroplist.DragNDropCursorAdapter;
import com.terlici.dragndroplist.DragNDropListView;
import com.terlici.dragndroplist.DragNDropSimpleAdapter;
import com.woxthebox.draglistview.DragItemAdapter;
import com.woxthebox.draglistview.DragListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 *  Inneholder startlisten og funksjonalitet for å stryke/flytte på løpere.
 *  Vurder å bytte ut expandable list view med bare vanlig listview som åpner en ny liste i et fragment.
 */

public class StartListActivity extends Fragment {

    private StartList startList;
    public ListView list;
    DragListView dragListView;

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

    /**
     * Extract info from the startlist ocject to populate the list views
     */
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
        createListItems();
        list = (ListView)v.findViewById(R.id.startlist);
        listView = (ExpandableListView)v.findViewById(R.id.expandable_list);
        dragListView = (DragListView) v.findViewById(R.id.drag_list);
        dragListView.setLayoutManager(new LinearLayoutManager(getActivity()));


        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                String distance = headers.get(groupPosition);
                ArrayList<Skater> list = listChildren.get(distance);
                ItemAdapter adapter = new ItemAdapter(list, R.layout.list_child_item, R.id.handler, true);
                dragListView.setAdapter(adapter, true);

                // dette lager listen under hele distanselista, ikke innimellom
                return true;
            }
        });


        setList();
        startSwipeListener();
        //startDragListener();
        return v;
    }

    public void setList(){

        // tror at dragListView ikke har noen adapter og jeg får derfor bare group view

        adapter = getExpandableAdapter(headers, listChildren);
        adapter.getGroupView(0, false, dragListView, listView);

        listView.setAdapter(adapter);

    }

    public DistanceListAdapter getExpandableAdapter(List<String> headers, HashMap<String, ArrayList<Skater>> listChildData){
        return new DistanceListAdapter(getContext(), headers, listChildData);
    }

    /**
     * Swipe detector for removing skaters from startlist
     */
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

    /**
     * Listens to drag events on the startlist by long click,
     * for changing the order in which the skaters appear.
     */
    public void startDragListener(){
        dragListView.setDragListListener(new DragListView.DragListListener() {
            @Override
            public void onItemDragStarted(int position) {
                //System.out.println(position);
            }

            @Override
            public void onItemDragEnded(int fromPosition, int toPosition) {
                System.out.println("From: "+fromPosition+" To: "+toPosition);
            }
        });

/*
        final DragDetector dragListener = new DragDetector();

        listView.setOnDragListener(dragListener);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //System.out.println(parent.indexOfChild(view));
                // send med all nødvendig info om løperen som dras rundt

                ClipData data = ClipData.newPlainText("test", "hils");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
                return true;
            }
        });
        */
    }
}
