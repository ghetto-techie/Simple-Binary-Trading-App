package com.big.shamba.ui.viewmodels.livedata;


import android.app.Application;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

import androidx.lifecycle.LiveData;

public class NetworkLiveData extends LiveData<Boolean> {
    private final ConnectivityManager connectivityManager;

    public NetworkLiveData(Application application) {
        connectivityManager = (ConnectivityManager) application.getSystemService(Application.CONNECTIVITY_SERVICE);
    }

    @Override
    protected void onActive() {
        super.onActive();

        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        connectivityManager.unregisterNetworkCallback(networkCallback);
    }

    private final ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            postValue(true);
        }

        @Override
        public void onLost(Network network) {
            postValue(false);
        }
    };
}
