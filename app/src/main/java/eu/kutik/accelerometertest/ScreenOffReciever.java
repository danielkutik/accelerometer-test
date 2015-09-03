package eu.kutik.accelerometertest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenOffReciever extends BroadcastReceiver {

  public void onReceive(final Context context, final Intent intent) {
    Log.wtf("FOO", "ScreenOff");
    if (context != null) {
      //context.getSharedPreferences()
    }
    TestService.start(context, intent);
  }

}
