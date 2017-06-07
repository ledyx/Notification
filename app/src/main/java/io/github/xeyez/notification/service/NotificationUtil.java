package io.github.xeyez.notification.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import java.lang.reflect.Method;

import io.github.xeyez.notification.R;

/**
 * Created by xeyez on 2016-05-14.
 */
public class NotificationUtil {
    public static Notification createNotification(Context context, PendingIntent pendingIntent, String title, String text, int iconId) {
        Notification notification;
        if (isNotificationBuilderSupported()) {
            notification = buildNotificationWithBuilder(context, pendingIntent, title, text, iconId);
        } else {
            notification = buildNotificationPreHoneycomb(context, pendingIntent, title, text, iconId);
        }
        return notification;
    }

    public static Notification createNotification(Context context, RemoteViews remoteViews, PendingIntent pendingIntent, int iconId) {
        try {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(iconId)
                    .setContent(remoteViews)
                    .setContentIntent(pendingIntent);

            return builder.build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static boolean isNotificationBuilderSupported() {
        try {
            return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) && Class.forName("android.app.Notification$Builder") != null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }


    @SuppressWarnings("deprecation")
    private static Notification buildNotificationPreHoneycomb(Context context, PendingIntent pendingIntent, String title, String text, int iconId) {
        Notification notification = new Notification(iconId, "", System.currentTimeMillis());
        try {
            // try to call "setLatestEventInfo" if available
            Method m = notification.getClass().getMethod("setLatestEventInfo", Context.class, CharSequence.class, CharSequence.class, PendingIntent.class);
            m.invoke(notification, context, title, text, pendingIntent);
        } catch (Exception e) {
            // do nothing
        }
        return notification;
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static Notification buildNotificationWithBuilder(Context context, PendingIntent pendingIntent, String title, String text, int iconId) {
        try {
            Notification.Builder builder = new Notification.Builder(context)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setContentIntent(pendingIntent)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.bell))
                    .setSmallIcon(iconId);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                builder = builder.setPriority(Notification.PRIORITY_MAX);
                return builder.build();
            } else {
                return builder.getNotification();
            }
        } catch (Exception e) {
        }

        return null;
    }
}