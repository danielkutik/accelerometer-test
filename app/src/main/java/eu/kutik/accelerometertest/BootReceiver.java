package eu.kutik.accelerometertest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public final class BootReceiver extends BroadcastReceiver {

  private static final String TAG = BroadcastReceiver.class.getSimpleName();

  @Override
  public void onReceive(final Context context, final Intent intent) {
    TestService.start(context, intent);
  }

}
