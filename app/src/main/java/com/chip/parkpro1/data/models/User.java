package com.chip.parkpro1.data.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.chip.parkpro1.R;
import com.google.gson.Gson;

public class User {

    public String firstName;
    public String lastName;
    public String email;
    public String mobile;
    public String countryDialCode;
    public String profilePic;
    public String cardId;
    public String card;
    public String balance;
    public String expiryMonth;
    public String expiryYear;
    public String holderName;
    public String plateNumber;
    public String carModel;
    public String pkeyGuidCar;

    public String token;
    public String topic;
    public double walletBalance;
    //todo remove it
    public String expiryDate;
    public String currency;
    public int defaultPaymentMethod;

    public static User getUser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        String json = prefs.getString("User", null);
        return new Gson().fromJson(json, User.class);
    }

    public static void setUser(Context context, String user) {
        SharedPreferences.Editor editor = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE).edit();
        editor.putString("User", user);
        editor.apply();
    }


}