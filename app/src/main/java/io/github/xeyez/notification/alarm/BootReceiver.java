package io.github.xeyez.notification.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EReceiver;
import org.joda.time.LocalTime;

import io.github.xeyez.notification.persistence.PreferencesHelper;
import io.github.xeyez.notification.service.MyService_;

/**
 * Created by Administrator on 2017-06-02.
 */

@EReceiver
public class BootReceiver extends BroadcastReceiver {

    @Bean
    PreferencesHelper preferencesHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
            return;

        int nowMills = LocalTime.now().getMillisOfDay();
        int startMills = preferencesHelper.getInt("startMills");
        int endMills = preferencesHelper.getInt("endMills");

        Log.d(getClass().getSimpleName(), startMills + " / " + endMills);

        if(startMills > nowMills || nowMills > endMills)
            return;

        Log.d(getClass().getSimpleName(), "This is now!");

        //context.startService(new Intent(context, MyService.class));
        MyService_.intent(context).start();
    }
}
