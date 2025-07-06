package com.example.product_sale_app.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.product_sale_app.R;
import com.example.product_sale_app.ui.cart.CartActivity;

public class BadgeUtils {
    private static final String CHANNEL_ID = "cart_notification_channel";
    private static final int NOTIFICATION_ID = 1001;
    private static final String CART_COUNT_PREF = "cart_count_pref";

    public static void updateBadgeCount(Context context, int count) {
        // Save count to SharedPreferences
        saveCartCount(context, count);

        if (count > 0) {
            createOrUpdateNotification(context, count);
        } else {
            clearNotification(context);
        }
    }

    private static void createOrUpdateNotification(Context context, int count) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Cart Notifications",
                    NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Shows the number of items in your cart");
            channel.setShowBadge(true);
            notificationManager.createNotificationChannel(channel);
        }

        // Create intent that opens CartActivity when notification is tapped
        Intent intent = new Intent(context, CartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.cart_icon)
                .setContentTitle("Shopping Cart")
                .setContentText(count + " " + (count == 1 ? "item" : "items") + " in your cart")
                .setNumber(count)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOngoing(false)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        // Post the notification
        notificationManager.notify(NOTIFICATION_ID, builder.build());
        Log.d("BadgeUtils", "Notification updated with count: " + count);
    }

    private static void clearNotification(Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
        Log.d("BadgeUtils", "Notification cleared");
    }

    private static void saveCartCount(Context context, int count) {
        SharedPreferences preferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        preferences.edit().putInt(CART_COUNT_PREF, count).apply();
    }

    public static int getCartCount(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        return preferences.getInt(CART_COUNT_PREF, 0);
    }
}