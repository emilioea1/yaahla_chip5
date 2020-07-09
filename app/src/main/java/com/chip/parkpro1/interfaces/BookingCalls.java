package com.chip.parkpro1.interfaces;

import android.view.View;

import com.chip.parkpro1.data.models.Site;

public interface BookingCalls {
    void onConfirmBooking(View v, Site site);
    void onBarcodeCalled(View v);
}