package com.example.wayfuel.Utils;

import static android.os.Build.VERSION_CODES.R;

import static com.example.wayfuel.Utils.CommonConstants.SHARED_PREF_NAME;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.wayfuel.Activity.Notification_Activity;
import com.example.wayfuel.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class NotificationService extends FirebaseMessagingService {

    int  not_id = 0;
    int count = 0;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    String title = "", body = "";
    NotificationCompat.Builder notificationBuilder;


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(com.example.wayfuel.R.string.firebase_token), s);
        editor.apply();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        count++;

        Map<String, String> receivedMap = remoteMessage.getData();

        title = remoteMessage.getNotification().getTitle();
        body = remoteMessage.getNotification().getBody();
        String icon = "";
        String type = remoteMessage.getMessageType();

        sendNotification(title, body, icon, type, receivedMap);

    }

    private void sendNotification(String title, String messageBody, String image, String type, Map<String, String> map) {

        int counter = MyPrefs.getInstance(getApplicationContext()).getInt("notifycount");
        MyPrefs.getInstance(getApplicationContext()).putInt("notifycount", counter + 1);

        Bitmap bitmap = null;

        try {

            URL url = new URL(image);
            bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(this, Notification_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(this,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(this,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }


        Bitmap icon = BitmapFactory.decodeResource(getResources(), com.example.wayfuel.R.mipmap.ic_launcher);
        NotificationCompat.BigPictureStyle s = new NotificationCompat.BigPictureStyle().bigPicture(bitmap);
        s.setSummaryText(messageBody);
        String CHANNEL_ID = "my_channel_01";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(com.example.wayfuel.R.mipmap.ic_launcher)
                .setLargeIcon(icon)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setContentText(messageBody)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(messageBody));




        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = getString(com.example.wayfuel.R.string.app_name);
                String description = getString(com.example.wayfuel.R.string.app_name);
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                notificationManager.createNotificationChannel(channel);
            }

            notificationManager.notify(not_id, notificationBuilder.build());
        }

        not_id++;
    }

    private void playSound(int soundResourceId) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this, soundResourceId);
        mediaPlayer.start();
        notificationBuilder.setDefaults(0);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }
            }
        }, 30000);
    }

}
