package edu.iitj.epsilon_netdemo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;

/**
 * Created by user on 15-11-2016.
 */
public class Utils {

    public static boolean isNetworkConnected(Context applicationContext) {
        ConnectivityManager cm = (ConnectivityManager)applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
    }

    public static String toString(ArrayList list) {
        String ret = "";
        for(int i=0; i<list.size(); i++) {
            ret += list.get(i).toString();
            if(i != list.size()-1)
                ret += " ";
        }
        return ret;
    }

}
