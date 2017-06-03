package io.github.xeyez.notification.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;
import org.joda.time.LocalTime;

import io.github.xeyez.notification.service.MyService;

/**
 * Created by Administrator on 2017-06-02.
 */

@EBean
public class AlaramBuilder {

    private static final int REQUEST_CODE = 111;

    @RootContext
    Context context;

    @SystemService
    AlarmManager alarmManager;

    public void execute(LocalTime startTime, LocalTime endTime) {
        LocalTime now = LocalTime.now();
        if(startTime.getMillisOfDay() > now.getMillisOfDay() || now.getMillisOfDay() > endTime.getMillisOfDay())
            return;

        int diff = endTime.getMillisOfDay() - startTime.getMillisOfDay();
        Log.d("diff?", String.valueOf(diff));

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, new Intent(context, AlarmReceiver.class), 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
        }

        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 0, pendingIntent);
    }

    public void cancel() {
        alarmManager.cancel(PendingIntent.getBroadcast(context, REQUEST_CODE, new Intent(context, AlarmReceiver.class), 0));
        context.stopService(new Intent(context, MyService.class));
    }
}
