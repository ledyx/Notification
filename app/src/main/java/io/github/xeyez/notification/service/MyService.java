package io.github.xeyez.notification.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;

import io.github.xeyez.notification.MainActivity_;
import io.github.xeyez.notification.R;

public class MyService extends Service implements ServiceTask.OnServiceTaskListener {

    //private NotificationManager notificationManager;
    private PendingIntent pendingIntent;
    private ServiceTask serviceTask;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("start", "start");

        if(pendingIntent == null) {
            Intent notificationIntent = new Intent(MyService.this, MainActivity_.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(MyService.this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        serviceTask = new ServiceTask(this);
        serviceTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("destroy", "destroy");

        if(serviceTask != null) {
            serviceTask.cancel(true);
            serviceTask = null;
        }

        /*if(notificationManager != null) {
            notificationManager.cancelAll();
        }*/
        stopForeground(true);
    }

    @Override
    public void onProgressServiceTask(LocalDateTime now) {
        Notification notification = NotificationUtil.createNotification(getApplicationContext(), pendingIntent, "Clock", now.toString(DateTimeFormat.fullDateTime()), R.drawable.bell);
        notification.flags |= Notification.FLAG_NO_CLEAR;

        /*if(new Random().nextInt(4) == 1) {
            notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
        }*/

        //notificationManager.notify(777, notification);
        startForeground(777, notification);
    }

    @Override
    public void onErrorServiceTask(Exception e) {
        e.printStackTrace();
    }
}
