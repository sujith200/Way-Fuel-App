package com.example.wayfuel.Utils;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

public class NotificationPermissionHelper {
    public static boolean isNotificationPermissionGranted(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            return notificationManager != null && notificationManager.getNotificationChannel("") != null
                    && notificationManager.getNotificationChannel("").getImportance() != NotificationManager.IMPORTANCE_NONE;
        }
        return true; // Notification permissions not needed before Android Oreo
    }

    public static void openNotificationSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Add this flag
        context.startActivity(intent);
    }
}
