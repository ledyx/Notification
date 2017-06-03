package io.github.xeyez.notification;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TimePicker;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.joda.time.LocalTime;

import io.github.xeyez.notification.alarm.AlaramBuilder;
import io.github.xeyez.notification.persistence.MyPrefs_;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    @Pref
    MyPrefs_ myPrefs;

    @ViewById
    TimePicker timepicker_alarm1, timepicker_alarm2;

    @Bean
    AlaramBuilder alaramBuilder;

    @Click({R.id.btn_startService, R.id.btn_cancelService})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_startService :
                LocalTime startTime = getTimeFromTimePicker(timepicker_alarm1);
                LocalTime endTime = getTimeFromTimePicker(timepicker_alarm2);

                /*myPrefs.startMills().put(startTime.getMillisOfDay());
                myPrefs.endMills().put(endTime.getMillisOfDay());*/

                alaramBuilder.set(startTime, endTime);
                break;

            case R.id.btn_cancelService :
                alaramBuilder.cancel();
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
}
