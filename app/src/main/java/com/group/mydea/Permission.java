package com.group.mydea;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;


public class Permission {

    public static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    public static final int MY_PERMISSIONS_REQUEST_STORAGE = 2;

    public static boolean needsToAskForPermissions(Activity activity) {
        return Build.VERSION.SDK_INT >= 23 &&
                (!isPermissionGranted(activity, Manifest.permission.RECORD_AUDIO) &&
                        !isPermissionGranted(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                        !isPermissionGranted(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                );
    }

    public static void askForPermissions(Activity activity) {
        // applicabile solo se siamo su marshmallow
        if (needsToAskForPermissions(activity)) {


            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST_RECORD_AUDIO);

            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_STORAGE);

            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_STORAGE);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

    private static boolean isPermissionGranted(Activity activity, String permission) {
        return ContextCompat.checkSelfPermission(activity, permission)
                == PackageManager.PERMISSION_GRANTED;
    }
}