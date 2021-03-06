package com.rajaraman.interceptincomingcall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;

public class PhoneStateChangedReceiver extends BroadcastReceiver {

  Bundle extras;

  @Override
  public void onReceive(final Context context, Intent intent) {
    // TODO Auto-generated method stub

    // Bundle extras = intent.getExtras();
    extras = intent.getExtras();

    if (extras != null) {

      String state = extras.getString(TelephonyManager.EXTRA_STATE);

      if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
        new Handler().postDelayed(new Runnable() {
          public void run() {
            Intent intentPhoneCall = new Intent("android.intent.action.ANSWER");

            String strIncomingNumber = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            intentPhoneCall.putExtra("INCOMING_NUMBER", strIncomingNumber);

            intentPhoneCall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentPhoneCall);
          }
        }, 2000);
      }
    }
  }
}
