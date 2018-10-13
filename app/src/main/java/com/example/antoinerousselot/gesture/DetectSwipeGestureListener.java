package com.example.antoinerousselot.gesture;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

import com.example.antoinerousselot.testvolley.MainActivity;

/**
 * Created by Jerry on 4/18/2018.
 */

public class DetectSwipeGestureListener extends GestureDetector.SimpleOnGestureListener {

    // Minimal x and y axis swipe distance.
    private static int MIN_SWIPE_DISTANCE_X = 1000;
    private static int MIN_SWIPE_DISTANCE_Y = 200;

    // Maximal x and y axis swipe distance.
    private static int MAX_SWIPE_DISTANCE_X = 2000;
    private static int MAX_SWIPE_DISTANCE_Y = 1000;

    // Source activity that display message in text view.
    private MainActivity activity = null;

    public MainActivity getActivity() {
        return activity;
    }

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }

    /* This method is invoked when a swipe gesture happened. */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        // Get swipe delta value in x axis.
        float deltaX = e1.getX() - e2.getX();

        // Get swipe delta value in y axis.
        float deltaY = e1.getY() - e2.getY();

        // Get absolute value.
        float deltaXAbs = Math.abs(deltaX);
        float deltaYAbs = Math.abs(deltaY);

        // Only when swipe distance between minimal and maximal distance value then we treat it as effective swipe
        if ((deltaXAbs >= MIN_SWIPE_DISTANCE_X) && (deltaXAbs <= MAX_SWIPE_DISTANCE_X)) {
            if (deltaX > 0) {
                Toast.makeText(this.activity,"Swipe à gauche",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this.activity,"Swipe à droite",Toast.LENGTH_SHORT).show();
            }
        }

        if ((deltaYAbs >= MIN_SWIPE_DISTANCE_Y) && (deltaYAbs <= MAX_SWIPE_DISTANCE_Y)) {
            if (deltaY > 0) {
                Toast.makeText(this.activity,"Swipe en haut",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this.activity,"Swipe en bas",Toast.LENGTH_SHORT).show();
            }
        }


        return true;
    }

}

