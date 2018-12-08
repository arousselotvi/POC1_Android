package com.example.antoinerousselot.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.RestrictTo;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.reflect.Method;
import java.util.HashMap;

public class NetworkController {
    private final String TAG = "NetworkController";

    boolean showDialog = true;
    private ProgressDialog progressDialog;

    private static NetworkController controller = new NetworkController();

    public static NetworkController getInstance()
    {
        return controller;
    }


    /**
     * Call this method if you want to show/hide loader
     *
     * @param showDialog default value = true
     *
     * */
    public void showDialog(boolean showDialog) {
        this.showDialog = showDialog;
    }


    /**
     * This method is responsible to communicate with server
     *
     * @param context current class context
     * @param method GET or POST
     * @param requestCode to identify request
     * @param resultListener to get callback for response
     * @param stringParams can be null if method is GET
     *
     * */
    public void connect(Context context, final int requestCode, int method, HashMap<String, String> stringParams, final ResultListener resultListener)
    {
        try
        {
            if (CheckNetworkState.isOnline(context)) {
                if (showDialog)
                    showDialog(context);

                String url = getUrl(requestCode);

                NetworkRequest networkRequest = new NetworkRequest(context, url, method, stringParams, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.e(TAG, "onResponse() called");
                        try {
                            resultListener.onResult(requestCode, true, jsonObject, null, progressDialog);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e(TAG, "onErrorResponse() called");
                                try {
                                    resultListener.onResult(requestCode, false, null, error, progressDialog);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                error.printStackTrace();
                            }
                        });

                networkRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Volley.newRequestQueue(context).add(networkRequest);
            }
            else
            {
                Toast.makeText(context, "Network not available!", Toast.LENGTH_SHORT).show();
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * To get url of requested code
     *
     * @param requestCode
     * */
    private String getUrl(int requestCode) {
        String url = "";
        switch (requestCode)
        {
            case UrlConstants.GET_URL_REQUEST_CODE:
                url = UrlConstants.GET_URL;
                break;
            case UrlConstants.POST_URL_REQUEST_CODE:
                url = UrlConstants.POST_URL;
                break;
            case UrlConstants.POST_URL_AUTHPLAYER1_REQUEST_CODE:
                url = UrlConstants.POST_URL_AUTH_PLAYER1;
                break;
            case UrlConstants.POST_URL_TEXT_REQUEST_CODE:
                url = UrlConstants.POST_URL_TEXT;
                break;
        }
        return url;
    }

    private void showDialog(Context context)
    {
        try
        {
            if (progressDialog != null && progressDialog.isShowing())
            {
                progressDialog.dismiss();
            }

            progressDialog = new ProgressDialog(context);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public interface ResultListener {

        void onResult(int requestCode, boolean isSuccess, JSONObject jsonObject, VolleyError volleyError, ProgressDialog progressDialog) throws JSONException;

    }

}
