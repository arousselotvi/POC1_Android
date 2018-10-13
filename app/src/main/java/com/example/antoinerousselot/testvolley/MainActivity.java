package com.example.antoinerousselot.testvolley;


import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.example.antoinerousselot.barcode.BarcodeCaptureActivity;
import com.example.antoinerousselot.gesture.DetectSwipeGestureListener;
import com.example.antoinerousselot.network.UrlConstants;
import com.example.antoinerousselot.network.NetworkController;
import com.google.android.gms.common.api.CommonStatusCodes;

import org.json.JSONObject;

import java.util.HashMap;

import static com.example.antoinerousselot.network.UrlConstants.GET_URL_REQUEST_CODE;
import static com.example.antoinerousselot.network.UrlConstants.POST_URL_REQUEST_CODE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NetworkController.ResultListener,GestureDetector.OnDoubleTapListener{

    Button getButton;
    Button postButton;
    Button barcodeButton;
    TextView responseTv;

    //For Gesture detection
    private static final String DEBUG_TAG = "Gestures";
    private GestureDetectorCompat mDetector;

    //For Barcode scanning
    private int REQUEST_CODE=100;

    // This is the gesture detector compat instance.
    private GestureDetectorCompat gestureDetectorCompat = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getButton = (Button) findViewById(R.id.getButton);
        postButton = (Button) findViewById(R.id.postButton);
        barcodeButton = (Button) findViewById(R.id.barcodeButton);
        responseTv = (TextView) findViewById(R.id.response);

        getButton.setOnClickListener(this);
        postButton.setOnClickListener(this);
        barcodeButton.setOnClickListener(this);

        // Create a common gesture listener object.
        DetectSwipeGestureListener gestureListener = new DetectSwipeGestureListener();

        // Set activity in the listener.
        gestureListener.setActivity(this);

        // Create the gesture detector with the gesture listener.
        gestureDetectorCompat = new GestureDetectorCompat(this, gestureListener);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.getButton:

                NetworkController.getInstance().connect(this, GET_URL_REQUEST_CODE, Request.Method.GET, null, this);

                break;
            case R.id.postButton:

                HashMap<String, String> stringParams = new HashMap<>();
                stringParams.put("imageURL", "https://www.google.fr/url?sa=i&rct=j&q=&esrc=s&source=images&cd=&cad=rja&uact=8&ved=2ahUKEwiC99ql6_ndAhXuzoUKHWcyBdIQjRx6BAgBEAU&url=http%3A%2F%2Fwww.allocine.fr%2Fpersonne%2Ffichepersonne-9081%2Fphotos%2Fdetail%2F%3Fcmediafile%3D20179869&psig=AOvVaw1oASgkvcFUSiwIr2raXHTJ&ust=1539190957368080");

                NetworkController.getInstance().connect(this, POST_URL_REQUEST_CODE, Request.Method.POST, stringParams, this);

                break;
            case R.id.barcodeButton:
                Intent intentBarcode = new Intent(this,BarcodeCaptureActivity.class);
                startActivityForResult(intentBarcode,REQUEST_CODE);

                break;
        }
    }

    @Override
    public void onResult(int requestCode, boolean isSuccess, JSONObject jsonObject, VolleyError volleyError, ProgressDialog progressDialog) {

        if (requestCode == POST_URL_REQUEST_CODE)
        {
            if (isSuccess)
            {
                Log.e("MainActivity", "onResult() called 2");
                responseTv.setText(jsonObject.toString());
            }
            else
            {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == GET_URL_REQUEST_CODE)
        {
            if (isSuccess)
            {
                Log.e("MainActivity", "onResult() called 1");
                responseTv.setText(jsonObject.toString());
            }
        }

        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == CommonStatusCodes.SUCCESS && requestCode == REQUEST_CODE){
            if (data != null && data.hasExtra("barcode")){
                Toast.makeText(this,data.getStringExtra("barcode"),Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (this.gestureDetectorCompat.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        Log.d(DEBUG_TAG, "onDoubleTap: " + event.toString());
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        Log.d(DEBUG_TAG, "onDoubleTapEvent: " + event.toString());
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        Log.d(DEBUG_TAG, "onSingleTapConfirmed: " + event.toString());
        return true;
    }


}