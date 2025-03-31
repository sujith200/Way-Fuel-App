package com.example.wayfuel.Utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.wayfuel.API.APICallbacks;
import com.example.wayfuel.API.APIStatus;
import com.example.wayfuel.API.UserData;
import com.example.wayfuel.MainActivity;
import com.example.wayfuel.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MyForegroundService extends Service {
    private static final String CHANNEL_ID = "ForegroundServiceChannel";
    public int counter = 0;
    //Activity activity;
    private GpsTracker gpsTracker;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        String activityPackage = intent.getStringExtra("activity_package");
        String activityClass = intent.getStringExtra("activity_class");

        if (activityPackage != null && activityClass != null) {
            try {
                Class<?> cls = Class.forName(activityClass);
                Intent notificationIntent = new Intent(this, cls);
                notificationIntent.setPackage(activityPackage);

                // Rest of your notification code
                createNotificationChannel();
                //Intent notificationIntent = new Intent(this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, notificationIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

                Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle("Way Fuel")
                        .setContentText("")
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentIntent(pendingIntent)
                        .build();

                startForeground(1, notification);

                startTimer();
            } catch (ClassNotFoundException e) {
                // e.printStackTrace();
            }
        }


        // Your background tasks go here

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private Timer timer;
    private TimerTask timerTask;
    private Handler handler = new Handler(Looper.getMainLooper());

    public void startTimer() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Your task code here
                Log.i("Count", "=========  " + (counter++));
                isNetworkConnected();
                if (isNetworkConnected()) {
                    Log.i("isNetworkConnected", "=========  " + isNetworkConnected());
                } else {
                    Log.i("isNetworkConnected", "=========  " + isNetworkConnected());
                }

                if (MyPrefs.getInstance(getApplicationContext()).getBoolean("ordPic")){
                    getLocation();
                }


                // Reschedule the task
                handler.postDelayed(this, 10 * 1000);
            }
        }, 0); // Start immediately
    }


    public void stopTimer() {
        handler.removeCallbacksAndMessages(null);
    }


    public void getLocation() {
        gpsTracker = new GpsTracker(getApplicationContext());
        //Log.i("DGPS", "getLocation: "+gpsTracker);

        if (gpsTracker.canGetLocation()) {
            String latitude = String.valueOf(gpsTracker.getLatitude());
            String longitude = String.valueOf(gpsTracker.getLongitude());

            Log.i("Location : ", latitude + " || " + longitude);
              callLocationApi(latitude, longitude);

        } else {
            gpsTracker.showSettingsAlert();
        }
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    void callLocationApi(String lat, String longe) {
        Map<String, String> map = new HashMap<>();
        map.put("CustID", MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_USER_ID));
        map.put("latitude", lat);
        map.put("longitude", longe);
        NetworkController.getInstance().callApiPost(MainActivity.activity, APPConstants.MAIN_URL + "UpdateLatLong", map, "location", new Bundle(), apiCallbacks);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Foreground Service Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    APICallbacks apiCallbacks = new APICallbacks() {
        @Override
        public void taskProgress(String tag, int progress, Bundle bundle) {

        }

        @Override
        public void taskFinish(APIStatus apiStatus, String tag, JSONObject response, String message, Bundle bundle) {
            if (apiStatus == APIStatus.SUCCESS) {

            }
        }
    };

}