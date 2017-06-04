package io.github.xeyez.notification.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EReceiver;

/**
 * Created by Administrator on 2017-06-02.
 */

@EReceiver
public class BootReceiver extends BroadcastReceiver {

    @Bean
    AlarmBuilder alarmBuilder;

    @Override
    public void onReceive(Context context, Intent intent) {
        /*if (!intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
            return;*/

        Log.d(getClass().getSimpleName(), "Reboot!");
        alarmBuilder.setForReboot();
    }
}
