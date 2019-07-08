package com.example.lugares.Volleys;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class Volleys {
    private static Volleys mVolleys = null;

    private RequestQueue requestQueue;

    private Volleys(Context context){
        requestQueue = Volley.newRequestQueue(context);
    }

    public static Volleys getInstance(Context context){
        if(mVolleys == null){
            mVolleys = new Volleys(context);
        }
        return mVolleys;
    }

    public RequestQueue getRequestQueue(){
        return requestQueue;
    }
}
