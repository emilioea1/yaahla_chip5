package com.chip.parkpro1.backgroundTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.chip.parkpro1.ServerCalls;
import com.chip.parkpro1.data.models.User;

import java.util.concurrent.Callable;

public class GetuserAsyncTask extends AsyncTask<String, Integer, String> {

    private static final String TAG = GetuserAsyncTask.class.getSimpleName();
    @SuppressLint("StaticFieldLeak")
    private Context mContext;
    public GetuserAsyncTask(Context context) {
        this.mContext = context;
    }
    @Override
    protected String doInBackground(String... strings) {
        ServerCalls.getUser(mContext, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Log.e(TAG,"Failed to get user information From Callable Function");
                throw new Exception("Failed to get user information From Callable Function");
            }
        });
        return User.getUser(mContext).topic;
    }
}
