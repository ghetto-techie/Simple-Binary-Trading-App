package com.big.shamba.utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

public class NetworkReceiver extends BroadcastReceiver {
    private static final String TAG = "NetworkReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            if (!NetworkUtils.isConnectedToInternet(context)) {
                Toast.makeText(context, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onReceive: No internet connection");
            }
        }
    }
}
