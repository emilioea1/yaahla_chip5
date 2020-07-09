package com.chip.parkpro1.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;
import com.chip.parkpro1.R;
import com.google.android.material.snackbar.Snackbar;
import com.irozon.sneaker.Sneaker;
import com.irozon.sneaker.interfaces.OnSneakerClickListener;
import com.irozon.sneaker.interfaces.OnSneakerDismissListener;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

public class Utils {

    public static void showDialog(Context context, String title, String message, String pos, String neg,
                                  DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setNegativeButton(neg, cancelListener);
        builder.setPositiveButton(pos, okListener);

        AlertDialog alert = builder.create();
        alert.show();
        Button negative = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        //negative.setBackgroundColor(context.getResources().getColor(R.color.white));
       // negative.setTextColor(context.getResources().getColor(R.color.appColor));

        Button positive = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        //positive.setBackgroundColor(context.getResources().getColor(R.color.white));
        //positive.setTextColor(context.getResources().getColor(R.color.appColor));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(20,0,0,0);
        positive.setLayoutParams(params);


    }

    public static void showTopSnackBar(Activity activity, String message) {
        TSnackbar snackbar = TSnackbar.make(activity.findViewById(android.R.id.content), message, TSnackbar.LENGTH_LONG);
        View view = snackbar.getView();
        view.setBackgroundColor(activity.getResources().getColor(android.R.color.black));
        TextView textView = view.findViewById(R.id.snackbar_text);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public static void showBottomSnackBar(Activity activity, String message) {
        Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        View view = snackbar.getView();
        view.setBackgroundColor(activity.getResources().getColor(R.color.appColor));
        TextView textView = view.findViewById(R.id.snackbar_text);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public static void showKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static void hideKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (view == null)
            view = activity.getCurrentFocus();
        if (view != null)
            view.clearFocus();
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean isPhoneNumberValid(Context context, String number, String code) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.createInstance(context);
        try {
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(number, code);
            return phoneUtil.isValidNumber(numberProto);
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
        }
        return false;
    }

    public static boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static boolean isInternetConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public static void addEventToCalendar(Context context, String description, String place, long startDate, long endDate) {
        String eventUriString = "content://com.android.calendar/events";
        ContentValues eventValues = new ContentValues();
        eventValues.put("calendar_id", 1);
        eventValues.put("title", "Parking Reservation");
        eventValues.put("description", description);
        eventValues.put("eventLocation", place);
        eventValues.put("dtstart", startDate);
        eventValues.put("dtend", endDate);
        eventValues.put("eventTimezone", "UTC/GMT +2:00");
        eventValues.put("hasAlarm", 1);
        Uri eventUri = context.getContentResolver().insert(Uri.parse(eventUriString), eventValues);
        long eventID = Long.parseLong(eventUri.getLastPathSegment());

        // For reminder
        String reminderUriString = "content://com.android.calendar/reminders";
        ContentValues reminderValues = new ContentValues();
        reminderValues.put("event_id", eventID);
        reminderValues.put("minutes", 45);
        reminderValues.put("method", 1);
        context.getContentResolver().insert(Uri.parse(reminderUriString), reminderValues);
    }

    public static String getLocationName(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Address obj = addresses.get(0);
            return obj.getAddressLine(0);
        } catch (IOException e) {
            return null;
        }
    }

    public static void disableClicks(final View view) {
        view.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        }, 1000);
    }

    public static void showSneaker(Activity activity, String title, String message, boolean autoHide, int duration, OnSneakerClickListener clickListener, OnSneakerDismissListener dismissListener, int color) {
        Sneaker.with(activity)
                .setTitle(title)
                .setMessage(message)
                .setDuration(duration)
                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setIcon(R.drawable.ic_error,R.color.white,true)
                .setOnSneakerClickListener(clickListener)
                .setOnSneakerDismissListener(dismissListener)
                .sneak(color);
    }

    public enum SneakerType {
        WARNING,
        SUCCESS,
        ERROR,
        DEFAULT
    }

    private static void showSneakerError(Activity activity, String title, String message) {
         Sneaker.with(activity)
                .setTitle(title)
                .setMessage(message)
                 .setDuration(60000)
                .sneakError();
    }

    private static void showSneakerSuccess(Activity activity, String title, String message) {
        Sneaker.with(activity)
                .setTitle(title)
                .setMessage(message)
                .setDuration(6000)
                .sneakSuccess();
    }

    public static void showSneaker(Activity activity, String title, String message, SneakerType type) {
        switch (type) {
            case ERROR:
                showSneakerError(activity,title,message);
                break;
            case SUCCESS:
                showSneakerSuccess(activity,title,message);
                break;
        }
    }

    public static KProgressHUD showLoading(Activity activity, KProgressHUD.Style style,String label, boolean cancellable, int animationSpeed) {
      return KProgressHUD.create(activity)
                .setStyle(style)
                .setLabel(label)
                .setCancellable(cancellable)
                .setAnimationSpeed(animationSpeed)
                .setDimAmount(0.5f)
                .show();
    }
}