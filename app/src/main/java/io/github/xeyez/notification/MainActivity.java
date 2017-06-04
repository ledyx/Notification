package io.github.xeyez.notification;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
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
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

import io.github.xeyez.notification.alarm.AlarmBuilder;
import io.github.xeyez.notification.persistence.PreferencesHelper;
import io.github.xeyez.notification.service.MyService;
import io.github.xeyez.notification.service.MyService_;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity implements MyService.OnMyServiceListener {

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

    @AfterViews
    void afterViews() {
        if(isMyServiceRunning(MyService_.class) || preferencesHelper.getBoolean("isSetAlarm")) {
            Log.d(getClass().getSimpleName(), preferencesHelper.getInt("startMills") + " / " + preferencesHelper.getInt("endMills"));

            setTimeToTimePicker(timepicker_alarm1, preferencesHelper.getInt("startMills"));
            setTimeToTimePicker(timepicker_alarm2, preferencesHelper.getInt("endMills"));
            setViewsEnabled(true);
        }
        else
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
                break;

            case R.id.btn_cancelService :
                alaramBuilder.cancel();

                setViewsEnabled(false);
                preferencesHelper.putBoolean("isSetAlarm", false);
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

    private void setTimeToTimePicker(TimePicker timePicker, int millis) {

        LocalTime localTime = LocalTime.now().withMillisOfDay(millis);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(localTime.getHourOfDay());
            timePicker.setMinute(localTime.getMinuteOfHour());
        }
        else {
            timePicker.setCurrentHour(localTime.getHourOfDay());
            timePicker.setCurrentMinute(localTime.getMinuteOfHour());
        }
    }


    private void setViewsEnabled(boolean isStart) {
        btn_startService.setEnabled(!isStart);
        btn_cancelService.setEnabled(isStart);

        timepicker_alarm1.setEnabled(!isStart);
        timepicker_alarm2.setEnabled(!isStart);

        if(!isStart)
            tv_time.setText("time");
    }

    @Override
    protected void onStart() {
        super.onStart();

        MyService.registerListener(getClass(), this);
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

    @Override
    public void onProgressMyService(int millisOfDay) {
        new Handler(Looper.getMainLooper()).post(() -> tv_time.setText(DateTime.now().withMillisOfDay(millisOfDay).toString(DateTimeFormat.fullDateTime())));
    }

    @Override
    public void onStopMyService() {
        new Handler(Looper.getMainLooper()).post(() -> setViewsEnabled(false));
        preferencesHelper.putBoolean("isSetAlarm", false);
    }
}
