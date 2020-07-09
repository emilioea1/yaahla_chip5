package com.chip.parkpro1;

import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import mobi.inthepocket.android.beacons.ibeaconscanner.Beacon;
import mobi.inthepocket.android.beacons.ibeaconscanner.BluetoothScanBroadcastReceiver;

public class BeaconActivityService extends JobIntentService {
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        // This is the beacon object containing UUID, major and minor info
        final Beacon beacon = intent.getParcelableExtra(BluetoothScanBroadcastReceiver.IBEACON_SCAN_BEACON_DETECTION);

        // This flag will be true if it is an enter event that triggered this service
        final boolean enteredBeacon = intent.getBooleanExtra(BluetoothScanBroadcastReceiver.IBEACON_SCAN_BEACON_ENTERED, false);

        // This flag will be true if it is an exit event that triggered this service
        final boolean exitedBeacon = intent.getBooleanExtra(BluetoothScanBroadcastReceiver.IBEACON_SCAN_BEACON_EXITED, false);

        //do shit when the beacon triggers
        if (enteredBeacon)
            Toast.makeText(this, "ENTRY", Toast.LENGTH_SHORT).show();
        if (exitedBeacon)
            Toast.makeText(this, "EXIT", Toast.LENGTH_SHORT).show();

    }
}