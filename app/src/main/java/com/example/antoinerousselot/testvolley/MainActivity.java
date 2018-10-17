package com.example.antoinerousselot.testvolley;


import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.example.antoinerousselot.barcode.BarcodeCaptureActivity;
import com.example.antoinerousselot.network.UrlConstants;
import com.example.antoinerousselot.network.NetworkController;
import com.google.android.gms.common.api.CommonStatusCodes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.example.antoinerousselot.network.UrlConstants.GET_URL_REQUEST_CODE;
import static com.example.antoinerousselot.network.UrlConstants.POST_URL_AUTHPLAYER1_REQUEST_CODE;
import static com.example.antoinerousselot.network.UrlConstants.POST_URL_REQUEST_CODE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NetworkController.ResultListener{

    Button getButton;
    Button postButton;
    Button barcodeButton;
    TextView responseTv;

    //For Gesture detection
    private static final String DEBUG_TAG = "Gestures";

    //For Barcode scanning
    private int REQUEST_CODE=100;

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
    public void onResult(int requestCode, boolean isSuccess, JSONObject jsonObject, VolleyError volleyError, ProgressDialog progressDialog) throws JSONException {

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
        else if (requestCode == POST_URL_AUTHPLAYER1_REQUEST_CODE)
        {
            if (isSuccess)
            {
                Log.e("MainActivity", "onResult() called 3");

                String resultatAuth = jsonObject.getString("AuthStatus");

                if (resultatAuth.equals("AuthFailed")) {
                    Toast.makeText(this, "Authentication failed, try again", Toast.LENGTH_SHORT).show();
                    Intent intentBarcodeRetry = new Intent(this,BarcodeCaptureActivity.class);
                    startActivityForResult(intentBarcodeRetry,REQUEST_CODE);
                }
                else {
                    Toast.makeText(this, "Welcome to paradise TOTEM", Toast.LENGTH_SHORT).show();
                    Intent openSecondAct = new Intent(this,SecondActivity.class);
                    startActivity(openSecondAct);
                }
            }

        }

        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == CommonStatusCodes.SUCCESS && requestCode == REQUEST_CODE){
            if (data != null && data.hasExtra("barcode")){
                //Toast.makeText(this,data.getStringExtra("barcode"),Toast.LENGTH_SHORT).show();
                HashMap<String, String> stringParamsAuth = new HashMap<>();
                stringParamsAuth.put("barcodeSent", data.getStringExtra("barcode"));
                NetworkController.getInstance().connect(this, POST_URL_AUTHPLAYER1_REQUEST_CODE, Request.Method.POST, stringParamsAuth, this);
            }
        }
    }



}