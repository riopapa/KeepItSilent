package com.urrecliner.andriod.keepitsilent;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

class PermissionCheck {

    private Activity thisActivity;

    boolean isAllPermitted(Activity activity) {
        thisActivity = activity;
        return isPermitted(Manifest.permission.WRITE_EXTERNAL_STORAGE) && isPermitted(Manifest.permission.RECEIVE_BOOT_COMPLETED);
    }

    private boolean isPermitted (String permission) {
        if (ContextCompat.checkSelfPermission(thisActivity, permission)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity, permission)) {
                return true;
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                ActivityCompat.requestPermissions(thisActivity, new String[]{permission},
                        1111);
                return isPermitted(permission);            }
        }
        return true;
    }
}
