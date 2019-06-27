package com.sargis.kh.apixu.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sargis.kh.apixu.App;

public class NetworkHelper {
    public static boolean isNetworkActive() {
        ConnectivityManager connMgr = (ConnectivityManager) App.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
