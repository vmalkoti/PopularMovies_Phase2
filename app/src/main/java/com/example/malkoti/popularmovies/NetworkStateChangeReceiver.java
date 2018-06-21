package com.example.malkoti.popularmovies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkStateChangeReceiver extends BroadcastReceiver {
    ConnectionChangeHandler connectionChangeHandler;

    public interface ConnectionChangeHandler {
        void performAction(boolean isConnected);
    }

    public NetworkStateChangeReceiver(ConnectionChangeHandler handler) {
        this.connectionChangeHandler = handler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.connectionChangeHandler.performAction(isOnline(context));
    }

    public boolean isOnline(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isConnected = (networkInfo != null) && networkInfo.isConnectedOrConnecting();

        return isConnected;
    }
}
