package eu.kutik.accelerometertest;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Process;
import android.support.annotation.Nullable;
import android.util.Log;


public class TestService extends Service {

  private static final String TAG = TestService.class.getSimpleName();

  private static final int SENSOR_DELAY = SensorManager.SENSOR_DELAY_NORMAL;
  private static final int SCREEN_OFF_DELAY = 50; // maybe a little higher value here

  private TestListener listener = null;

  private BroadcastReceiver screenEventReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (intent != null) {
        if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
          onScreenOff();
        } else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
          onScreenOn();
        }
      }
    }
  };

  Thread screenOffThread = null;
  final Runnable screenOffRunnable = new Runnable() {
    @Override
    public void run() {
      //noinspection InfiniteLoopStatement
      while (screenOffThread != null) {
        //NO LOGGING inside this loop!
        registerDropListenerScreenOff();
        try {
          Thread.sleep(SCREEN_OFF_DELAY);
        } catch (InterruptedException ignore) { }
        unregisterDropListener();
      }
    }
  };

  private void startScreenOffThread() {
    screenOffThread = new Thread(screenOffRunnable);
    screenOffThread.start();
  }

  private void stopScreenOffThread() {
    if (screenOffThread != null) {
      if (!screenOffThread.isInterrupted()) {
        screenOffThread.interrupt();
      }
      screenOffThread = null;
    }
  }


  private void onScreenOff() {
    unregisterDropListener();
    stopScreenOffThread();
    startScreenOffThread();
  }

  private void onScreenOn() {
    stopScreenOffThread();
    unregisterDropListener();
    registerDropListener();
  }

  private PowerManager.WakeLock sensorWakeLock;

  @Nullable
  private SensorManager sensorManager = null;

  public static void start(@Nullable final Context context) {
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

    startForeground(Process.myPid(), new Notification());

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
    // TODO check if the service starts while screen is off
    registerScreenEventReciever();
    //aquireWakeLock();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    unregisterScreenEventReciever();
    unregisterDropListener();
    //releaseWakeLock();
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

  private void registerDropListenerScreenOff() {
    if (sensorManager != null && listener != null) {
      sensorManager.registerListener(listener,
          sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
          SensorManager.SENSOR_DELAY_FASTEST);
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

  private void registerScreenEventReciever() {
    final IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
    intentFilter.addAction(Intent.ACTION_SCREEN_ON);
    registerReceiver(screenEventReceiver, intentFilter);
  }

  private void unregisterScreenEventReciever() {
    unregisterReceiver(screenEventReceiver);
  }

  private void aquireWakeLock() {
    if (sensorWakeLock == null) {
      final PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
      if (pm != null) {
        sensorWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
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
