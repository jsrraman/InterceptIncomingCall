package com.rajaraman.interceptincomingcall;

import java.lang.reflect.Method;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.android.internal.telephony.ITelephony;
import com.rajaraman.interceptincomingcall.IncomingCallService.LocalBinder;

public class IncomingCallActivity extends Activity {

  private boolean mBound = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_incoming_call);

    Log.d(Constants.TAG, "Starting incoming call service");

    bindService(new Intent(this, IncomingCallService.class), mConnection, Context.BIND_AUTO_CREATE);

    Intent startServiceIntent = new Intent(this, IncomingCallService.class);
    startService(startServiceIntent);

    Button btn_pickup_call = (Button) findViewById(R.id.btn_pickup_call);

    btn_pickup_call.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        answerPhoneHeadsetHook();
        // answerPhoneHeadsetHook1(v.getContext());
      }
    });

    Button btn_hangup_call = (Button) findViewById(R.id.btn_hangup_call);

    btn_hangup_call.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        disconnectPhoneItelephony(v.getContext());
      }
    });
  }

  @Override
  protected void onResume() {

    super.onResume();

    Log.d(Constants.TAG, "onResume start");

    Bundle extras = getIntent().getExtras();

    if (extras == null) {
      return;
    }

    // Get data via the key
    String incoming_number = extras.getString("INCOMING_NUMBER");

    if (incoming_number != null) {

      TextView txt_call_status = (TextView) findViewById(R.id.txt_call_status);
      txt_call_status.setText("Incoming Call from " + incoming_number);
    }

    Log.d(Constants.TAG, "onResume end");
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.incoming_call, menu);
    return true;
  }

  void answerPhoneHeadsetHook() {
    Log.d(Constants.TAG, "answerPhoneHeadsetHook start");

    Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);

    buttonUp.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP,
        KeyEvent.KEYCODE_HEADSETHOOK));

    this.sendOrderedBroadcast(buttonUp, "android.permission.CALL_PRIVILEGED");

    Log.d(Constants.TAG, "Call Answered");
  }

  // This is another way of answering the call but this does not work without
  // having android.permission.MODIFY_PHONE_STATE declared but this is not
  // allowed from 4.2.2
  // void answerPhoneHeadsetHook1(Context context) {
  // ITelephony telephonyService;
  // Log.v(TAG, "Receving....");
  //
  // TelephonyManager telephony = (TelephonyManager) context
  // .getSystemService(Context.TELEPHONY_SERVICE);
  // try {
  // Log.v(TAG, "Get getTeleService...");
  //
  // Class c = Class.forName(telephony.getClass().getName());
  // Method m = c.getDeclaredMethod("getITelephony");
  //
  // m.setAccessible(true);
  // telephonyService = (ITelephony) m.invoke(telephony);
  // telephonyService.silenceRinger();
  //
  // Log.v(TAG, "Answering Call now...");
  // telephonyService.answerRingingCall();
  // Log.v(TAG, "Call answered...");
  //
  // // telephonyService.endCall();
  // } catch (Exception e) {
  // e.printStackTrace();
  // Log.e(TAG, "FATAL ERROR: could not connect to telephony subsystem");
  // Log.e(TAG, "Exception object: " + e);
  // }
  // }

  public static void disconnectPhoneItelephony(Context context) {
    ITelephony telephonyService;

    Log.v(Constants.TAG, "Now disconnecting using ITelephony....");

    TelephonyManager telephony =
        (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    try {
      Log.v(Constants.TAG, "Get getTeleService...");

      Class c = Class.forName(telephony.getClass().getName());

      Method m = c.getDeclaredMethod("getITelephony");
      m.setAccessible(true);
      telephonyService = (ITelephony) m.invoke(telephony);

      // telephonyService.silenceRinger();
      Log.v(Constants.TAG, "Disconnecting Call now...");

      // telephonyService.answerRingingCall();
      // telephonyService.endcall();

      Log.v(Constants.TAG, "Call disconnected...");
      telephonyService.endCall();
    } catch (Exception e) {
      e.printStackTrace();
      Log.e(Constants.TAG, "FATAL ERROR: could not connect to telephony subsystem");
      Log.e(Constants.TAG, "Exception object: " + e);
    }
  }

  /** Defines callbacks for service binding, passed to bindService() */
  private ServiceConnection mConnection = new ServiceConnection() {

    @Override
    public void onServiceConnected(ComponentName className, IBinder service) {
      // We've bound to LocalService, cast the IBinder and get
      // LocalService instance
      LocalBinder binder = (LocalBinder) service;
      mBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName arg0) {
      mBound = false;
    }
  };

  protected void onDestroy() {
    super.onDestroy();

    // Unbind from the service
    if (mBound) {
      unbindService(mConnection);
      mBound = false;
    }
  }
}
