package com.example.mylocationtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "GeofenceReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        String transition = "";
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            transition = "Enter Geofence";

        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            transition = "Leave Geofence";
        }

        Toast.makeText(context, transition, Toast.LENGTH_SHORT).show();
        Log.d(TAG, transition);
    }
}
