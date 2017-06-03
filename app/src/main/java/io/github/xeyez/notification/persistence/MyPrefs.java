package io.github.xeyez.notification.persistence;

import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by Administrator on 2017-06-03.
 */

@SharedPref
public interface MyPrefs {
    int startMills();
    int endMills();
}
