package eu.kutik.accelerometertest;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public abstract class TestListener implements SensorEventListener {

  @Override
  public final void onSensorChanged(final SensorEvent event) {
    if (event != null && event.sensor != null
        && Sensor.TYPE_ACCELEROMETER == event.sensor.getType()) {
      onAccelerometerChanged(event);
    }
  }

  abstract void onAccelerometerChanged(final SensorEvent state);

  @Override
  public final void onAccuracyChanged(final Sensor sensor, final int accuracy) {
    //ignored
  }

}
