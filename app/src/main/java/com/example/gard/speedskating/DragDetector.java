package com.example.gard.speedskating;

import android.app.ExpandableListActivity;
import android.content.ClipData;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;


public class DragDetector implements View.OnDragListener {

    @Override
    public boolean onDrag(View v, DragEvent event) {
        int action = event.getAction();

        float startX = 0;
        float startY = 0;
        float endX;
        float endY;

        // husk break i hver case for å hindre at noe skjer to ganger
        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:
                System.out.println("Drag started");
                View dragStartView = (View) event.getLocalState();
                ViewGroup dragStartParent = (ViewGroup) dragStartView.getParent();
                startX = event.getX();
                startY = event.getY();

                // denne indeksen blir fem, jeg aner ikke hvorfor
                //int childIndex = dragStartParent.indexOfChild(dragStartView);
                break;
            case DragEvent.ACTION_DROP:
                endX = event.getX();
                endY = event.getY();

                ClipData data = event.getClipData();
                LinearLayout view = (LinearLayout) event.getLocalState();

                ViewGroup parent = (ViewGroup) view.getParent();
                ExpandableListView container = (ExpandableListView) view.getParent();
                int childIndex = container.indexOfChild(view);

                int index = getIndex(startX, startY, endX, endY, childIndex);
                int firstPos = container.getFirstVisiblePosition();
                int height = view.getHeight();

                System.out.println("Dropped at index: "+height+" "+firstPos);
                break;
        }
        return true;
    }

    int getIndex(float startX, float startY, float endX, float endY, int startIndex) {
        boolean positive = getScrollDirection(startY, endY);
        System.out.println("StartX: "+startX+" EndX: "+endX);
        System.out.println("StartY: "+startY+" EndY: "+endY);
        System.out.println("Positive? "+positive);

        //if()
        return 0;
    }

    boolean getScrollDirection(float from, float to) {
        if(to - from > 0) return true;
        return false;
    }
}
