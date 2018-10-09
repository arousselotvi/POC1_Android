package com.example.antoinerousselot.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class CheckNetworkState {

    /**
     * Check whether device has internet connection or not
     *
     * @param context
     * */
    public static boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        NetworkInfo wifiNetwork = cm
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        NetworkInfo mobileNetwork = cm
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        NetworkInfo otherNetwork = cm.getActiveNetworkInfo();

        boolean isWifi = false;
        boolean isOtherNetwork = false;

        if (wifiNetwork != null) {
            isWifi = wifiNetwork.isConnectedOrConnecting();
        }

        if (mobileNetwork != null) {
            isOtherNetwork = mobileNetwork.isConnectedOrConnecting();
        }

        if (activeNetwork != null) {
            isOtherNetwork = activeNetwork.isConnectedOrConnecting();

        }

        return isWifi || isOtherNetwork;
    }
}
