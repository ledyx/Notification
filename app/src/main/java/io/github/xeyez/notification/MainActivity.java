package io.github.xeyez.notification;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
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

    public static final int REQUEST_RINGTONE = 123;

    @ViewById
    Button btn_startService;

    @ViewById
    Button btn_cancelService;

    @ViewById
    Button btn_ring;

    @ViewById
    TextView tv_time;

    @Bean
    PreferencesHelper preferencesHelper;

    @ViewById
    TimePicker timepicker_alarm1, timepicker_alarm2;

    @Bean
    AlarmBuilder alaramBuilder;

    private Uri notificationSoundUri = null;

    @AfterViews
    void afterViews() {
        if(isMyServiceRunning(MyService_.class) || preferencesHelper.getBoolean("isSetAlarm")) {
            Log.d(getClass().getSimpleName(), preferencesHelper.getInt("startMills") + " / " + preferencesHelper.getInt("endMills"));

            setTimeToTimePicker(timepicker_alarm1, preferencesHelper.getInt("startMills"));
            setTimeToTimePicker(timepicker_alarm2, preferencesHelper.getInt("endMills"));
            setViewsEnabled(true);
        }
        else {
            Log.wtf("??", preferencesHelper.getBoolean("isSetAlarm") + " / " + !alaramBuilder.isPassedTime());

            if(preferencesHelper.getBoolean("isSetAlarm") && !alaramBuilder.isPassedTime())
                setViewsEnabled(true);
            else {
                setViewsEnabled(false);
                preferencesHelper.putBoolean("isSetAlarm", false);
            }
        }
    }

    @Click({R.id.btn_startService, R.id.btn_cancelService, R.id.btn_ring})
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

            case R.id.btn_ring :
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, notificationSoundUri);
                startActivityForResult(intent, REQUEST_RINGTONE);
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

        btn_ring.setEnabled(!isStart);

        if(!isStart)
            tv_time.setText("time");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_RINGTONE && resultCode == RESULT_OK) {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            notificationSoundUri = uri;

            MyService.registerListener(getClass(), notificationSoundUri, this);

            String ringToneName = uri != null? RingtoneManager.getRingtone(this, notificationSoundUri).getTitle(this) : "없음";
            btn_ring.setText(ringToneName);

            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(Settings.System.canWrite(this)) {
                    RingtoneManager.setActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION, uri);
                }
                else {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 555);
                }
            }*/
        }
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
