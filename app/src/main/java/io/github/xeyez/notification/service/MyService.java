package io.github.xeyez.notification.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EService;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.concurrent.atomic.AtomicBoolean;

import io.github.xeyez.notification.MainActivity_;
import io.github.xeyez.notification.R;

@EService
public class MyService extends Service {
    private PendingIntent pendingIntent;
    private AtomicBoolean running;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(getClass().getSimpleName(), "onCreate");

        if(pendingIntent == null) {
            Intent notificationIntent = new Intent(MyService.this, MainActivity_.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        if(running == null)
            running = new AtomicBoolean(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("start", "start");

        workInBackground();

        return START_STICKY;
    }

    @Background
    void workInBackground() {
        try {
            running.set(true);

            while(running.get()) {
                Notification notification = NotificationUtil.createNotification(getApplicationContext(), pendingIntent, "Clock", LocalDateTime.now().toString(DateTimeFormat.fullDateTime()), R.drawable.bell);
                notification.flags |= Notification.FLAG_NO_CLEAR;

                /*if(new Random().nextInt(4) == 1) {
                    notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
                }*/

                //notificationManager.notify(777, notification);
                startForeground(777, notification);

                Log.d(getClass().getSimpleName(), "wtf?");

                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("destroy", "destroy");

        running.set(false);
        stopForeground(true);
    }
}