package com.rajaraman.interceptincomingcall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootCompletedReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {

    Log.d(Constants.TAG, "Boot completed event received");
    Intent startServiceIntent = new Intent(context, IncomingCallService.class);
    context.startService(startServiceIntent);
  }
}
