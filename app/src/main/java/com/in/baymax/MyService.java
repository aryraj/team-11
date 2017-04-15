package com.in.baymax;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyService extends Service {
    private LocationManager locManager;
    private SensorManager senManager;
    private LocationListener locListener = new myLocationListener();
    private SensorEventListener senListener = new mySensorListener();
    private Sensor sensor;
    static final Double EARTH_RADIUS = 6371.00;

    private boolean gps_enabled = false;
    private boolean network_enabled = false;

    private Handler handler = new Handler();
    Thread t;
    final static String MY_ACTION = "MY_ACTION";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public void onStart(Intent intent, int startid) {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(getBaseContext(), "Service Started", Toast.LENGTH_SHORT).show();

        final Runnable r = new Runnable() {
            public void run() {
                Log.v("Debug", "Hello");
                location();
                handler.postDelayed(this, 5000);
            }
        };
        handler.postDelayed(r, 5000);
        return START_STICKY;
    }

    public void location() {
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            gps_enabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }
        Log.v("Debug", "in on create.. 2");
        if (gps_enabled) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
            Log.v("Debug", "Enabled..");
        }
        if (network_enabled) {
            locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,locListener);
            Log.v("Debug", "Disabled..");
        }
        Log.v("Debug", "in on create..3");
    }

    private class mySensorListener implements SensorEventListener
    {

        @Override
        public void onSensorChanged(SensorEvent event) {

            float[] mGravity;
            double mAccelLast = SensorManager.GRAVITY_EARTH;
            double mAccelCurrent = SensorManager.GRAVITY_EARTH;
            double mAccel = 0.00f;


            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                mGravity = event.values.clone();

                float x = mGravity[0];
                float y = mGravity[1];
                float z = mGravity[2];

                mAccelLast = mAccelCurrent;
                mAccelCurrent = Math.sqrt(x * x + y * y + z * z);

                double delta = mAccelCurrent - mAccelLast;
                mAccel = mAccel * 0.9 + delta;

                Toast toast = new Toast(getApplicationContext());
                toast.makeText(getApplicationContext(), "Acceleration is: " + mAccel, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                if (mAccel > 20) {
                    Toast.makeText(getApplicationContext(), "Acceleration Detected!", Toast.LENGTH_SHORT).show();

                    startForeground(1398, buildNotification());
                    //vibrator.vibrate(1000);

                    // call sendmessage
                   // new SendMessage().execute();
                }

            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    private class myLocationListener implements LocationListener
    {
        double lat_old=0.0;
        double lon_old=0.0;
        double lat_new;
        double lon_new;
        double time=10;
        double speed=0.0;
        @Override
        public void onLocationChanged(Location location) {
            Log.v("Debug", "in onLocation changed..");
            if(location!=null){
                locManager.removeUpdates(locListener);
                //String Speed = "Device Speed: " +location.getSpeed();
                lat_new=location.getLongitude();
                lon_new =location.getLatitude();
                String longitude = "Longitude: " + location.getLongitude();
                String latitude = "Latitude: " + location.getLatitude();
                double distance = CalculationByDistance(lat_new, lon_new, lat_old, lon_old);
                speed = distance/time;
                Toast.makeText(getApplicationContext(), longitude+"\n"+latitude+"\nDistance is: "
                        +distance+"\nSpeed is: "+speed , Toast.LENGTH_SHORT).show();
                lat_old=lat_new;
                lon_old=lon_new;

                Intent intent = new Intent();
                intent.setAction(MY_ACTION);
                intent.putExtra("speed", speed);
                sendBroadcast(intent);

                if (speed >= 5){
                    startForeground(1338, buildNotification());
                }
            }
        }
        @Override
        public void onProviderDisabled(String provider) {}
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }

    private Notification buildNotification() {
        NotificationCompat.Builder b = new NotificationCompat.Builder(this);
        b.setOngoing(false);
        b.setContentTitle("Alert from baymax!");
        b.setContentText("Monitoring your trip!");
        b.setSmallIcon(R.drawable.ab);
        b.setTicker("ticker");


        return b.build();
    }

    public double CalculationByDistance(double lat1, double lon1, double lat2, double lon2) {
        double Radius = EARTH_RADIUS;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return Radius * c;
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

