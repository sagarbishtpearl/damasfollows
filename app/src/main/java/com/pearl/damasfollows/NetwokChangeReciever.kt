package com.pearl.damasfollows

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

class NetwokChangeReciever(activity: MainActivity):BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?)
        {
        if (isOnline(p0)){
        //    activity.checkNetwork(true);
        }
            else{
           // activity.checkNetwork(true);
            }
    }

    private fun isOnline(p0: Context?): Boolean {
        return try {
            val cm = p0!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            netInfo != null && netInfo.isConnected
        } catch (e: NullPointerException) {
            e.printStackTrace()
            false
        }
    }
    }




