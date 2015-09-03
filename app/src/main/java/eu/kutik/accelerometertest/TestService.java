package eu.kutik.accelerometertest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

public class TestService extends Service {

  private static final String TAG = TestService.class.getSimpleName();

  private static final int SENSOR_DELAY = SensorManager.SENSOR_DELAY_FASTEST;

  private TestListener listener = null;

  private ScreenOffReciever screenOffReceiver;

  private PowerManager.WakeLock sensorWakeLock;

  @Nullable
  private SensorManager sensorManager = null;

  public static void start(@Nullable final Context context, @Nullable final Intent intent) {
    Log.wtf(TAG, "#start");
    if (context != null) {
      try {
        final Intent serviceIntent = new Intent(context, TestService.class);
        context.startService(serviceIntent);
      } catch (final Throwable error) {
        Log.wtf(TAG, "Unexpected error", error);
      }
    }
  }

  @Nullable
  @Override
  public IBinder onBind(final Intent intent) {
    return null;
  }

  @Override
  public int onStartCommand(final Intent intent, final int flags, final int startId) {
    Log.wtf(TAG, ".onStartCommand");

    Runnable runnable = new Runnable() {
      public void run() {
        Log.wtf(TAG, "Delayed");
        reregisterDropListener();
      }
    };

    new Handler().postDelayed(runnable, 5000);

    // We want this service to continue running until it is explicitly
    // stopped, so return sticky.
    return START_STICKY;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    initSensorManager();
    initDropListener();
    registerDropListener();
    registerScreenOffReciever();
    aquireWakeLock();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    unregisterScreenOffReciever();
    unregisterDropListener();
    releaseWakeLock();
  }

  private void initDropListener() {
    listener = new TestListener() {

      private long last = System.currentTimeMillis();

      @Override
      void onAccelerometerChanged(final SensorEvent event) {
        long time = System.currentTimeMillis();
        long delta = time - last;
        last = time;
        Log.e(TAG, "\u0394t: " + delta + "ms");
      }
    };
  }

  private void registerDropListener() {
    if (sensorManager != null && listener != null) {
      sensorManager.registerListener(listener,
          sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
          SENSOR_DELAY);
    }
  }

  private void unregisterDropListener() {
    if (sensorManager != null && listener != null) {
      sensorManager.unregisterListener(listener);
    }
  }

  private void initSensorManager() {
    sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
  }

  private void reregisterDropListener() {
    //initSensorManager();
    unregisterDropListener();
    registerDropListener();
  }

  private void registerScreenOffReciever() {
    screenOffReceiver = new ScreenOffReciever();
    final IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
    registerReceiver(screenOffReceiver, intentFilter);
  }

  private void unregisterScreenOffReciever() {
    unregisterReceiver(screenOffReceiver);
  }

  private void aquireWakeLock() {
    if (sensorWakeLock == null) {
      final PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
      if (pm != null) {
        sensorWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "gsensor");
      }
    }
    if (sensorWakeLock != null) {
      sensorWakeLock.acquire();
    }
  }

  private void releaseWakeLock() {
    if (sensorWakeLock != null) {
      sensorWakeLock.release();
    }
  }

}
