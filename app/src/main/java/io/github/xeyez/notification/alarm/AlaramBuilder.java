package io.github.xeyez.notification.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import io.github.xeyez.notification.service.MyService;
import lombok.AllArgsConstructor;

/**
 * Created by Administrator on 2017-06-02.
 */

@AllArgsConstructor
public class AlaramBuilder {

    private static final int REQUEST_CODE = 111;

    private Context context;
    private AlarmManager alarmManager;

    public void execute(LocalTime startTime, LocalTime endTime) {
        DateTime now = DateTime.now();
        int nowHour = now.getHourOfDay();
        int nowMinute = now.getMinuteOfHour();

        boolean isAvailableHour = startTime.getHourOfDay() <= nowHour && nowHour <= endTime.getHourOfDay();
        boolean isAvailableMinute = startTime.getMinuteOfHour() <= nowMinute && nowMinute <= endTime.getMinuteOfHour();

        Log.d("wtf?", isAvailableHour + " / " + isAvailableMinute);

        if(!isAvailableHour)
            return;

        if(!isAvailableMinute)
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
