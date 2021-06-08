package com.rts.commonutils_2_0.netconnection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionDetector {
    /**
    * Check Internet connection.
    */
    public static boolean isConnectingToInternet(Context _context){
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
          if (connectivity != null) 
          {
              NetworkInfo activeinfo = connectivity.getActiveNetworkInfo();
              if (activeinfo != null)
                  if (activeinfo.getState() == NetworkInfo.State.CONNECTED)
                  {
                      return true;
                  }
          }
          return false;
    }



}
