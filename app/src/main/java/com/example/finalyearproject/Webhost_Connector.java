package com.example.finalyearproject;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/*
Class derived from the following sources:

[35]"Network Connectivity Events using NetworkCallback in Android || API 29 || Android 10",
Youtube.com, 2019. [Online].
Available: https://www.youtube.com/watch?v=Z9rCJTDQzdQ. [Accessed: 03- May- 2021].
 */



// class which handles outgoing requests to the backend - implements the Singleton class pattern, since only one instance is needed
public class Webhost_Connector {
    private static Webhost_Connector instance;
    private RequestQueue requestQueue;
    private static Context ctx;
    ConnectivityManager con;
    NetworkRequest netRequest;
    ConnectivityManager.NetworkCallback netCallBack;

    private Webhost_Connector(final Context context){
        ctx = context;
        requestQueue = getRequestQueue();

        // used for checking internet connection - signs user out if connection lost
        con = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        netRequest = new NetworkRequest.Builder().build();
        netCallBack = new ConnectivityManager.NetworkCallback(){
            @Override
            public void onLost(Network network) {
                // CODE TO EXECUTE IF CONNECTION LOST
                Toast.makeText(ctx, "Internet connection lost...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ctx, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
            }
        };
        con.registerNetworkCallback(netRequest, netCallBack);
    }

    public static synchronized Webhost_Connector getInstance(Context context) {
        if (instance == null) {
            instance = new Webhost_Connector(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addRequest(Request<T> request){
        getRequestQueue().add(request);
    }
}

