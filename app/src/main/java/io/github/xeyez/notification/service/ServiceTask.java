package io.github.xeyez.notification.service;

import android.os.AsyncTask;

import org.joda.time.LocalDateTime;

import lombok.AllArgsConstructor;

/**
 * Created by Administrator on 2017-06-02.
 */

@AllArgsConstructor
public class ServiceTask extends AsyncTask<Void, LocalDateTime, Void> {

    private static final int INTERVAL = 1000;

    public interface OnServiceTaskListener {
        void onProgressServiceTask(LocalDateTime now);
        void onErrorServiceTask(Exception e);
    }

    private OnServiceTaskListener onServiceTaskListener;

    @Override
    protected Void doInBackground(Void... params) {
        try {
            while(!isCancelled()) {
                publishProgress(LocalDateTime.now());
                Thread.sleep(INTERVAL);
            }
        } catch (Exception e) {
            cancel(true);

            if(onServiceTaskListener != null)
                onServiceTaskListener.onErrorServiceTask(e);
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(LocalDateTime... values) {
        super.onProgressUpdate(values);

        if(onServiceTaskListener != null) {
            try {
                onServiceTaskListener.onProgressServiceTask(values[0]);
            } catch (Exception e) {
                onServiceTaskListener.onErrorServiceTask(e);
            }
        }
    }

}
