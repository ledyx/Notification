package io.github.xeyez.notification.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import io.github.xeyez.notification.MainActivity_;
import io.github.xeyez.notification.R;
import io.github.xeyez.notification.persistence.PreferencesHelper;

@EService
public class MyService extends Service {

    public interface OnMyServiceListener {
        void onProgressMyService(int millisOfDay);
        void onStopMyService();
    }

    private static ConcurrentHashMap<String, OnMyServiceListener> observers = new ConcurrentHashMap<>();

    public static void registerListener(Class clazz, OnMyServiceListener onMyServiceListener) {
        observers.put(clazz.getName(), onMyServiceListener);
    }

    public static void unregisterListener(Class clazz) {
        if(!observers.containsKey(clazz.getName()))
            return;

        observers.remove(clazz.getName());
    }

    public static boolean isRegisteredListener(Class clazz) {
        return observers.containsKey(clazz.getName());
    }


    @Bean
    PreferencesHelper preferencesHelper;

    private PendingIntent pendingIntent;
    private AtomicBoolean running;

    private int startMills = 0;
    private int endMills = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Log.d(getClass().getSimpleName(), "onCreate");

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
        Log.d(getClass().getSimpleName(), "start");

        startMills = preferencesHelper.getInt("startMills");
        endMills = preferencesHelper.getInt("endMills");

        workInBackground();

        return START_STICKY;
        //return START_REDELIVER_INTENT;
    }

    @Background
    void workInBackground() {
        try {
            running.set(true);

            while(running.get()) {
                int nowMills = LocalTime.now().getMillisOfDay();
                if(startMills > nowMills || nowMills > endMills) {
                    onDestroy();
                    break;
                }

                //Log.d("test", "test");

                LocalDateTime now = LocalDateTime.now();

                Notification notification = NotificationUtil.createNotification(getApplicationContext(), pendingIntent, "Clock", now.toString(DateTimeFormat.fullDateTime()), R.drawable.bell);
                notification.flags |= Notification.FLAG_NO_CLEAR;

                /*if(new Random().nextInt(4) == 1) {
                    notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
                }*/

                //notificationManager.notify(777, notification);
                startForeground(777, notification);

                observers.values().forEach(onMyServiceListener -> onMyServiceListener.onProgressMyService(now.getMillisOfDay()));

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

        observers.values().forEach(OnMyServiceListener::onStopMyService);
    }
}