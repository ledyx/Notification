package io.github.xeyez.notification.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import io.github.xeyez.notification.service.MyService_;

/**
 * Created by Administrator on 2017-06-02.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(getClass().getSimpleName(), "Received");
        //context.startService(new Intent(context, MyService.class));
        MyService_.intent(context).start();
    }
}
