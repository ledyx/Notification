package io.github.xeyez.notification;

import android.app.AlarmManager;
import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TimePicker;

import org.androidannotations.annotations.AfterViews;
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

    private AlaramBuilder alaramBuilder;

    @AfterViews
    void afterViews() {
        //timepicker_alarm.setOnTimeChangedListener((view, hourOfDay, minute) -> Toast.makeText(this, hourOfDay + " / " + minute, Toast.LENGTH_SHORT).show());
        alaramBuilder = new AlaramBuilder(this, (AlarmManager) getSystemService(Context.ALARM_SERVICE));
    }

    @Click({R.id.btn_startService, R.id.btn_cancelService})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_startService :
                //startService(new Intent(this, MyService.class));
                LocalTime startTime = getTimeFromTimePicker(timepicker_alarm1);
                LocalTime endTime = getTimeFromTimePicker(timepicker_alarm2);

                /*myPrefs.startMills().put(startTime.getMillisOfDay());
                myPrefs.endMills().put(endTime.getMillisOfDay());*/

                alaramBuilder.execute(startTime, endTime);
                break;

            case R.id.btn_cancelService :
                //stopService(new Intent(this, MyService.class));
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
