package com.example.informatik.cognitizer.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionsHelper {

    private PermissionsHelper() {}

    private static final int permissionRequestId = 42;

    /**
     * Checks wheter all required Permissions are set. If not, requests them and returns false.
     * @return True if all permissions are received
     */
    public static  boolean checkAndGetPermissions(Activity activity) {

        List<String> missingPermissions = new ArrayList<>();

        addPermissionIfNotGranted(activity, missingPermissions, Manifest.permission.INTERNET);
        addPermissionIfNotGranted(activity, missingPermissions, Manifest.permission.READ_EXTERNAL_STORAGE);
        addPermissionIfNotGranted(activity, missingPermissions, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        addPermissionIfNotGranted(activity, missingPermissions, Manifest.permission.CAMERA);
        addPermissionIfNotGranted(activity, missingPermissions, Manifest.permission.RECORD_AUDIO);

        if(missingPermissions.size() > 0) {
            String[] permissionsToRequest = new String[missingPermissions.size()];

            for (int i = 0; i < missingPermissions.size(); i++) {
                permissionsToRequest[i] = missingPermissions.get(i);
            }

            activity.requestPermissions(permissionsToRequest, permissionRequestId);
            return false;
        } else {
            return true;
        }
    }

    private static void addPermissionIfNotGranted(Context context, List<String> missingPermissions, String permission) {
        if(ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            missingPermissions.add(permission);
        }
    }
}
