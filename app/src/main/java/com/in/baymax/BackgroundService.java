package com.in.baymax;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


import android.*;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Process;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BackgroundService extends Service implements SensorEventListener {


    private SensorManager mSensorManager;
    private Sensor sensor;
    private double mAccel;
    private double mAccelLast;
    private double mAccelCurrent;
    private float[] mGravity;
    private TextView textView, tv3, tv2, tv;
    private LocationManager mLocationManager;
    private Vibrator vibrator;
    AlertDialog alertBox;
    float x, y, z;
    double delta;
    SensorEventListener mAccelerationListener;
    private static int FOREGROUND_ID=1338;

    public static final String TAG = BackgroundService.class.getName();
    public static final int SCREEN_OFF_RECEIVER_DELAY = 500;
    private PowerManager.WakeLock mWakeLock;

    public BackgroundService() {
    }


    private void registerListener(){
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);

    }

    private void unRegisterListener(){
        mSensorManager.unregisterListener(this);
    }

    public BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive("+intent+")");

            if (!intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                return;
            }

            Runnable runnable = new Runnable() {
                public void run() {
                    Log.i(TAG, "Runnable executing.");
                    unRegisterListener();
                    registerListener();
                }
            };

            new Handler().postDelayed(runnable, SCREEN_OFF_RECEIVER_DELAY);
        }

    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate(){
        super.onCreate();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        PowerManager manager =
                (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);

        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));

    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        // new SensorEventMonitorTask().execute();

        //return super.onStartCommand(intent, flags, startId);

        Notification notification = new Notification();
        startForeground(FOREGROUND_ID, buildNotification() );
        registerListener();
        mWakeLock.acquire();

        return START_STICKY;

    }




    @Override
    public void onDestroy(){
        super.onDestroy();

        unregisterReceiver(mBroadcastReceiver);
        unRegisterListener();
        mWakeLock.release();
        stopForeground(true);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            mGravity = event.values.clone();

            float x = mGravity[0];
            float y = mGravity[1];
            float z = mGravity[2];

            mAccelLast = mAccelCurrent;
            mAccelCurrent = Math.sqrt(x * x + y * y + z * z);

            double delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9 + delta;



            if (mAccel > 20) {
                Toast.makeText(this, "Acceleration Detected!", Toast.LENGTH_SHORT).show();

                vibrator.vibrate(1000);

                // call sendmessage
                //new SendMessage().execute();


                //showAlert();


            }

        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }




    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            location.getLatitude();
            //Toast toast = Toast.makeText(MainActivity.this, "Current Speed: " + location.getSpeed(), Toast.LENGTH_SHORT);
            //toast.setGravity(Gravity.CENTER, 0, 0);
            //toast.show();
            //tv2.setText(String.valueOf(location.getSpeed()));

            double speed_initial = 40.00;

            if (!location.hasSpeed()){
                double speed_final = 0.0;//location.getSpeed();

                double dS = Math.abs(speed_initial - speed_final);

                if (dS >= 40 && mAccel > 10){
                    Toast.makeText(getBaseContext(), "Crash Detected!!", Toast.LENGTH_SHORT).show();
                }
            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    private class MyClass extends CountDownTimer {
        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        private MyClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            tv.setText("" + millisUntilFinished / 1000);


        }

        @Override
        public void onFinish() {
            alertBox.dismiss();
            Toast.makeText(getBaseContext(), "Contacts Alerted!", Toast.LENGTH_SHORT).show();

        }


    }

    private Notification buildNotification(){

        NotificationCompat.Builder b = new NotificationCompat.Builder(this);
        b.setOngoing(false);
        b.setContentTitle("Alert from baymax");
        b.setContentText("Monitoring your trip!");
        b.setSmallIcon(R.drawable.ab);
        b.setTicker("ticker");


        return b.build();
    }

    private class SendMessage extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String JsonStr = null;

            try {

                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL("https://control.msg91.com/api/sendhttp.php?authkey=142714AmcG6l7Cl58b183b8&mobiles=919972971606&message=Your friend was in an accident&sender=BAYMAX&route=4&country=0");

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                JsonStr = buffer.toString();
                return JsonStr;
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //tvWeatherJson.setText(s);
            Log.i("json", s);
        }
    }

}

