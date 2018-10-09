package com.example.antoinerousselot.testvolley;


import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.example.antoinerousselot.network.UrlConstants;
import com.example.antoinerousselot.network.NetworkController;

import org.json.JSONObject;

import java.util.HashMap;

import static com.example.antoinerousselot.network.UrlConstants.GET_URL_REQUEST_CODE;
import static com.example.antoinerousselot.network.UrlConstants.POST_URL_REQUEST_CODE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NetworkController.ResultListener{

    Button getButton;
    Button postButton;
    TextView responseTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getButton = (Button) findViewById(R.id.getButton);
        postButton = (Button) findViewById(R.id.postButton);
        responseTv = (TextView) findViewById(R.id.response);

        getButton.setOnClickListener(this);
        postButton.setOnClickListener(this);
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
}