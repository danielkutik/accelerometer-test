package eu.kutik.accelerometertest;

import android.app.Application;

public class TestApplication extends Application {

  private static final String TAG = TestApplication.class.getSimpleName();

  @Override
  public void onCreate() {
    super.onCreate();
    TestService.start(this, null);
  }

}
