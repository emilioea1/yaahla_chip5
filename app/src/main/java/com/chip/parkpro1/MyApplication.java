package com.chip.parkpro1;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.chip.parkpro1.backgroundTask.GetuserAsyncTask;
import com.chip.parkpro1.data.models.User;
import com.chip.parkpro1.utils.customViews.ShowLoading;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.sentry.Sentry;
import io.sentry.android.AndroidSentryClientFactory;

public class MyApplication extends Application {
    private static final String TAG = MyApplication.class.getSimpleName();
    CleverTapAPI cleverTapAPI;
    @SuppressLint("StaticFieldLeak")
    public static ShowLoading showLoading;
    private RequestQueue requestQueue;
    private static volatile MyApplication mInstance;
    private final GetuserAsyncTask getuserAsyncTask = new GetuserAsyncTask(MyApplication.this);

    @Override
    public void onCreate() {
        super.onCreate();

        handleSSLHandshake();
        showLoading = new ShowLoading();
        Fabric.with(this, new Crashlytics());
        String sentryDsn = "https://84a02b1eedcd469ab2144e13fe31a68a@sentry.io/1218688";
        Sentry.init(sentryDsn, new AndroidSentryClientFactory(this));
        mInstance = this;
        try {
            cleverTapAPI = CleverTapAPI.getInstance(getApplicationContext());
        } catch (CleverTapMetaDataNotFoundException | CleverTapPermissionsNotSatisfied ignored) {
        }

        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name("parkpro1.realm")
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
        if (User.getUser(this) != null && User.getUser(this).token != null) {
            try {
                String topic = getuserAsyncTask.execute("Download User").get();
                if (topic != null) {
                    subscribeTopic(topic);
                }else {
                    return;
                }

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }


        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.d(TAG, "getInstance Failed", task.getException());
                return;
            }
            String token = Objects.requireNonNull(task.getResult()).getToken();
            String msg = getString(R.string.msg_token_fmt, token);
            Log.d(TAG, "getInstance " + msg);
            Toast.makeText(MyApplication.this, msg, Toast.LENGTH_SHORT).show();
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }
    }

    public void subscribeTopic(final String topic) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic).addOnCompleteListener(task -> {
            String msg = getString(R.string.msg_subscribed, topic);
            if (!task.isSuccessful()) {
                if(!task.isSuccessful()) {
                    msg = getString(R.string.msg_subscribe_failed,topic);
                }
                Log.d(TAG, msg);
                Toast.makeText(MyApplication.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void unsubscribeTopic(final String topic){
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic).addOnCompleteListener(task -> {
            String msg = getString(R.string.msg_unsubscribed, topic);
            if (!task.isSuccessful()) {
                if(!task.isSuccessful()) {
                    msg = getString(R.string.msg_unsubscribe_failed,topic);
                }
                Log.d(TAG, msg);
                Toast.makeText(MyApplication.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static synchronized  MyApplication getInstance() {
        return mInstance;
    }

    /*
        public method to add the Request to the the single
        instance of RequestQueue created above.Setting a tag to every
        request helps in grouping them. Tags act as identifier
        for requests and can be used while cancelling them
     */
    public void addToRequestQueue(Request request, String tag) {
        request.setTag(tag);
        VolleyLog.d("Adding request to queue: %s", request.getUrl());
        request.setRetryPolicy(new DefaultRetryPolicy(10000,
                5,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue().add(request);
    }
    /*
    Cancel all the requests matching with the given tag
    */
    public void cancelAllRequests(String tag) {
        getRequestQueue().cancelAll(tag);
    }
    /*
    Create a getRequestQueue() method to return the instance of
    RequestQueue.This kind of implementation ensures that
    the variable is instantiated only once and the same
    instance is used throughout the application
    */
    public RequestQueue getRequestQueue() {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(this);
        return requestQueue;
    }
    /**
     * Enables https connections
     */
    @SuppressLint("TrulyRandom")
    public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((arg0, arg1) -> true);
        } catch (Exception ignored) {
        }
    }
}