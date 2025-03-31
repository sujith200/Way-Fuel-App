package com.example.wayfuel.Utils;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.LifecycleObserver;

import com.google.gson.Gson;

import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class MyApplication  extends Application implements CommonConstants , LifecycleObserver {
    public SQLiteDatabase sqLiteDatabase;
    private static MyApplication instance;
    private Gson gson = new Gson();
    private OkHttpClient okHttpClient;
    public static long TIMEOUT = 60;

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT * 5, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT * 5, TimeUnit.SECONDS)
                .build();

       // AndroidNetworking.initialize(getApplicationContext(), okHttpClient);

        // Initialize your SQLiteOpenHelper or database management here if needed.
        // For example:
        // DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        // sqLiteDatabase = dbHelper.getWritableDatabase();
    }

    public static boolean isAboveQ() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }

    public Gson getGson() {
        return gson;
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }
}
