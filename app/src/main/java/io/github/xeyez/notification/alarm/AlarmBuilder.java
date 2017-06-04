package io.github.xeyez.notification.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import io.github.xeyez.notification.persistence.PreferencesHelper;
import io.github.xeyez.notification.service.MyService_;

/**
 * Created by Administrator on 2017-06-02.
 */

@EBean(scope = EBean.Scope.Singleton)
public class AlarmBuilder {

    private static final int REQUEST_CODE = 111;

    @RootContext
    Context context;

    @SystemService
    AlarmManager alarmManager;

    @Bean
    PreferencesHelper preferencesHelper;

    private PendingIntent pendingIntent;

    @AfterInject
    void afterInject() {
        pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, new Intent(context, AlarmReceiver_.class), 0);
    }

    public void set(LocalTime startTime, LocalTime endTime) {
        if(endTime.getMillisOfDay() - startTime.getMillisOfDay() <= 0)
            return;

        preferencesHelper.putBoolean("isSetAlarm", true);
        preferencesHelper.putInt("startMills", startTime.getMillisOfDay());
        preferencesHelper.putInt("endMills", endTime.getMillisOfDay());

        Log.d(getClass().getSimpleName(), preferencesHelper.getInt("startMills") + " / " + preferencesHelper.getInt("endMills"));

        long millis = DateTime.now().withHourOfDay(startTime.getHourOfDay()).withMinuteOfHour(startTime.getMinuteOfHour()).getMillis();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, millis, pendingIntent);
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, millis, pendingIntent);
        else
            alarmManager.set(AlarmManager.RTC_WAKEUP, millis, pendingIntent);
    }

    public void cancel() {
        alarmManager.cancel(pendingIntent);
        MyService_.intent(context).stop();
    }
}
