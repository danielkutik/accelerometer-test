# Accelerometer Test

I tried to get accelerometer data during 'Sceen Off' on Android. On some devices
(Mediatek/Android 4.2?) the only way to get it done, was to
have a background thread keep re-registering the listener as long as the screen is off.

http://nosemaj.org/android-persistent-sensors
http://www.saltwebsites.com/2012/android-accelerometers-screen-off

https://code.google.com/p/android/issues/detail?id=3708
http://stackoverflow.com/questions/9982433/android-accelerometer-not-working-when-screen-is-turned-off
http://stackoverflow.com/questions/23334112/sensor-background-service-wakelock

http://stackoverflow.com/questions/12724292/late-suspend-early-resume-and-wakelocks-please-explain
http://stackoverflow.com/questions/14125775/cannot-keep-sensor-update-rate-after-screen-off
http://stackoverflow.com/questions/3926056/read-sensors-while-screen-is-black