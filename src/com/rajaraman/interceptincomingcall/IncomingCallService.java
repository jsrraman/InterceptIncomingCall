package com.rajaraman.interceptincomingcall;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

public class IncomingCallService extends Service {
  private final IBinder mBinder = new LocalBinder();
  private BroadcastReceiver telephonyBroadcastReceiver;
  private Bundle extras;

  @Override
  public void onCreate() {
    setupTelephonyManagerBroadcastReceiver();
  }

  @Override
  public void onDestroy() {
    unregisterReceiver(telephonyBroadcastReceiver);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d(Constants.TAG, "Incoming call service started");

    return Service.START_STICKY; // Revisit this flag later
  }

  @Override
  public IBinder onBind(Intent arg0) {
    return mBinder;
  }

  public class LocalBinder extends Binder {
    IncomingCallService getService() {
      return IncomingCallService.this;
    }
  }

  private void setupTelephonyManagerBroadcastReceiver() {
    telephonyBroadcastReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(final Context context, Intent intent) {

        Log.d(Constants.TAG, "Inside Telephony Manager broadcast onReceive");

        extras = intent.getExtras();

        if (extras != null) {

          String state = extras.getString(TelephonyManager.EXTRA_STATE);

          if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            
            handleIncomingCall(context); 

          } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {

          }
        }
      }
    };

    IntentFilter filter = new IntentFilter();
    filter.addAction(android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED);

    registerReceiver(telephonyBroadcastReceiver, filter);
  }
  
  void handleIncomingCall(final Context context) {
    
    new Handler().postDelayed(new Runnable() {
      
      public void run() {
        Intent intentPhoneCall = new Intent("android.intent.action.ANSWER");

        String incoming_number = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
        intentPhoneCall.putExtra("INCOMING_NUMBER", incoming_number);

        intentPhoneCall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intentPhoneCall);
      }
    }, 2000);

  }
}
