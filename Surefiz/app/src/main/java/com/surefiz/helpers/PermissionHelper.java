package com.surefiz.helpers;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class PermissionHelper implements ActivityCompat.OnRequestPermissionsResultCallback {

    public static final int PERMISSION_FINE_LOCATION = 1;
    public static final int PERMISSION_COARSE_LOCATION = 2;
    public static final int PERMISSION_READ_EXTERNAL_STORAGE = 3;
    public static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 4;

    private Activity contextActivity;

    public PermissionHelper(Activity contextActivity) {
        this.contextActivity = contextActivity;
    }

    public boolean checkPermission(int permission) {

        if (ActivityCompat.checkSelfPermission(contextActivity, getPermission(permission))
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    public void requestForPermission(final int permission) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(contextActivity,
                getPermission(permission))) {
            Log.d("PERMISSION", "-" + permission);

            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(contextActivity);
            if(permission == PERMISSION_FINE_LOCATION) {
                // Setting Dialog Message
                alertDialog.setMessage("Location Permission is required to list all available SSIDs.");
            }else {
                // Setting Dialog Message
                alertDialog.setMessage("Permission is required.");
            }

            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    ActivityCompat.requestPermissions(contextActivity, new String[]{getPermission(permission)}, permission);

                }
            });

            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to invoke NO event
                    dialog.dismiss();
                }
            });

            // Showing Alert Message
            alertDialog.show();
        } else {
            Log.d("PERMISSION", "-" + permission);
            ActivityCompat.requestPermissions(contextActivity, new String[]{getPermission(permission)}, permission);

        }
        // END_INCLUDE(camera_permission_request)
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {


        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Camera permission has been granted, preview can be displayed
            Log.d("PERMISSION", "-" +requestCode);

        } else {

        }
    }

    public String getPermission(int permis) {
        String permission = null;

        switch (permis) {
            case PERMISSION_FINE_LOCATION:
                permission = Manifest.permission.ACCESS_FINE_LOCATION;
                Log.d("PERMISSION", "-" + permission);
                return permission;
            case PERMISSION_READ_EXTERNAL_STORAGE:
                permission = Manifest.permission.READ_EXTERNAL_STORAGE;
                Log.d("PERMISSION", "-" + permission);
                return permission;
            case PERMISSION_WRITE_EXTERNAL_STORAGE:
                permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                Log.d("PERMISSION", "-" + permission);
                return permission;
        }
        return permission;
    }

}
