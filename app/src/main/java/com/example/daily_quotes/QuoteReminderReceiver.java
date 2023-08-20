package com.example.daily_quotes;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.app.PendingIntent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class QuoteReminderReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "quote_reminder_channel";
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        String quote = intent.getStringExtra("quote");
        showNotification(context, quote);
    }

    private void showNotification(Context context, String quote) {
        createNotificationChannel(context);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Quote of the Day")
                .setContentText(quote)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // Check if the app has the necessary permission
        if (checkNotificationPermission(context)) {
            // Show the notification
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } else {
            // Handle the case when the permission is not available
            Log.e("QuoteReminderReceiver", "Permission for showing notifications not granted");
        }
    }

    private boolean checkNotificationPermission(Context context) {
        String permission = "android.permission.ACCESS_NOTIFICATION_POLICY";
        int result = context.checkCallingOrSelfPermission(permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Quote Reminder";
            String description = "Daily quote reminder channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                try {
                    // Check if the app has the necessary permission
                    if (context.getPackageManager().checkPermission(android.Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                            context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                        // Create the notification channel
                        notificationManager.createNotificationChannel(channel);
                    } else {
                        // Handle the case when the permission is not available
                        Log.e("QuoteReminderReceiver", "Permission ACCESS_NOTIFICATION_POLICY not granted");
                    }
                } catch (SecurityException e) {
                    // Handle SecurityException
                    Log.e("QuoteReminderReceiver", "SecurityException while creating notification channel: " + e.getMessage());
                }
            }
        }
    }
}
