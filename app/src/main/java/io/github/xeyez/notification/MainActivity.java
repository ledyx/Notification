package io.github.xeyez.notification;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

import io.github.xeyez.notification.alarm.AlarmBuilder;
import io.github.xeyez.notification.persistence.PreferencesHelper;
import io.github.xeyez.notification.service.IRemoteService;
import io.github.xeyez.notification.service.IRemoteServiceCallback;
import io.github.xeyez.notification.service.MyService_;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    @ViewById
    Button btn_startService;

    @ViewById
    Button btn_cancelService;

    @ViewById
    TextView tv_time;

    @Bean
    PreferencesHelper preferencesHelper;

    @ViewById
    TimePicker timepicker_alarm1, timepicker_alarm2;

    @Bean
    AlarmBuilder alaramBuilder;

    IRemoteService remoteService;

    IRemoteServiceCallback.Stub callback = new IRemoteServiceCallback.Stub() {

        @Override
        public void onProgressService(int nowMillis) throws RemoteException {
            Log.d("onProgressService11", String.valueOf(nowMillis));

            try {
                LocalDateTime now = LocalDateTime.now().withMillisOfDay(nowMillis);

                new Handler(Looper.getMainLooper()).post(() -> tv_time.setText(now.toString(DateTimeFormat.fullDateTime())));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onStopService() throws RemoteException {
            setViewsEnabled(false);
        }
    };

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if(service == null)
                return;

            Log.d(getClass().getSimpleName(), "onServiceConnected!");

            remoteService = IRemoteService.Stub.asInterface(service);
            try {
                remoteService.registerCallback(callback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if(remoteService == null)
                return;

            Log.d(getClass().getSimpleName(), "onServiceDisconnected!");

            try {
                remoteService.unregisterCallback(callback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    @AfterViews
    void afterViews() {
        Log.d(getClass().getSimpleName(), preferencesHelper.getInt("startMills") + " / " + preferencesHelper.getInt("endMills"));
        setViewsEnabled(false);
    }

    @Click({R.id.btn_startService, R.id.btn_cancelService})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_startService :
                LocalTime startTime = getTimeFromTimePicker(timepicker_alarm1);
                LocalTime endTime = getTimeFromTimePicker(timepicker_alarm2);

                if(endTime.getMillisOfDay() - startTime.getMillisOfDay() <= 0) {
                    Toast.makeText(this, "Check TimePicker!", Toast.LENGTH_SHORT).show();
                    return;
                }

                alaramBuilder.set(startTime, endTime);

                setViewsEnabled(true);

                bindService(new Intent(this, MyService_.class), serviceConnection, BIND_AUTO_CREATE);
                break;

            case R.id.btn_cancelService :
                alaramBuilder.cancel();

                setViewsEnabled(false);

                tv_time.setText("time");
                unbindService(serviceConnection);
                break;
        }
    }

    private LocalTime getTimeFromTimePicker(TimePicker timePicker) {
        int hour;
        int minute;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hour = timePicker.getHour();
            minute = timePicker.getMinute();
        }
        else {
            hour = timePicker.getCurrentHour();
            minute = timePicker.getCurrentMinute();
        }

        return new LocalTime(hour, minute);
    }

    private void setViewsEnabled(boolean isStart) {
        btn_startService.setEnabled(!isStart);
        btn_cancelService.setEnabled(isStart);

        timepicker_alarm1.setEnabled(!isStart);
        timepicker_alarm2.setEnabled(!isStart);
    }

    @Override
    protected void onStop() {
        super.onStop();

        unbindService(serviceConnection);
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
