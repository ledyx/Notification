package io.github.xeyez.notification.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.joda.time.DateTime;

import io.github.xeyez.notification.service.MyService;

/**
 * Created by Administrator on 2017-06-02.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
            return;

        DateTime now = DateTime.now();
        int nowHour = now.getHourOfDay();
        int nowMinute = now.getMinuteOfHour();

        // 저장된 시간 필요

        context.startService(new Intent(context, MyService.class));
    }
}
