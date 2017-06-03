package io.github.xeyez.notification.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EReceiver;

import io.github.xeyez.notification.persistence.PreferencesHelper;
import io.github.xeyez.notification.service.MyService_;

/**
 * Created by Administrator on 2017-06-02.
 */

@EReceiver
public class AlarmReceiver extends BroadcastReceiver {

    @Bean
    PreferencesHelper preferencesHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.d(getClass().getSimpleName(), "Received " + intent.getAction());

        //Log.d(getClass().getSimpleName(), preferencesHelper.getInt("startMills") + " / " + preferencesHelper.getInt("endMills"));

        MyService_.intent(context).start();
    }
}
