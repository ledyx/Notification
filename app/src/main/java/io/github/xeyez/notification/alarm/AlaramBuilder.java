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
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.joda.time.LocalTime;

import io.github.xeyez.notification.persistence.MyPrefs_;
import io.github.xeyez.notification.service.MyService_;

/**
 * Created by Administrator on 2017-06-02.
 */

@EBean
public class AlaramBuilder {

    private static final int REQUEST_CODE = 111;

    @Pref
    MyPrefs_ myPrefs;

    @RootContext
    Context context;

    @SystemService
    AlarmManager alarmManager;

    public void set(LocalTime startTime, LocalTime endTime) {
        LocalTime now = LocalTime.now();
        if(startTime.getMillisOfDay() > now.getMillisOfDay() || now.getMillisOfDay() > endTime.getMillisOfDay())
            return;

        myPrefs.startMills().put(startTime.getMillisOfDay());
        myPrefs.endMills().put(endTime.getMillisOfDay());

        Log.d(getClass().getSimpleName(), myPrefs.startMills().get() + " / " + myPrefs.endMills().get());


        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, new Intent(context, AlarmReceiver.class), 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
        else
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
    }

    public void cancel() {
        alarmManager.cancel(PendingIntent.getBroadcast(context, REQUEST_CODE, new Intent(context, AlarmReceiver.class), 0));
        MyService_.intent(context).stop();
    }
}
