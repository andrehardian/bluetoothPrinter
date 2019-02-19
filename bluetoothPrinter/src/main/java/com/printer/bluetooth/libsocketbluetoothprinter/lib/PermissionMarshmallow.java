package com.printer.bluetooth.libsocketbluetoothprinter.lib;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import lombok.Setter;

/**
 * Created by AndreHF on 9/27/2016.
 */
@EBean
public class PermissionMarshmallow {
    @Setter
    @RootContext
    Activity activity;


    public static final int GET_ACCOUNTS_PERMISSION_REQUEST_CODE = 1;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 2;
    public static final int READ_EXTERNAL_STORAGE = 3;
    public static final int WRITE_PERMISSION_REQUEST_CODE = 4;
    public static final int IMEI_REQUEST_CODE = 5;
    public static final int CALL_REQUEST_CODE = 6;
    public static final int COARSE_LOCATION_PERMISSION_REQUEST_CODE = 7;
    public static final int FINE_LOCATION_PERMISSION_REQUEST_CODE = 8;
    public static final int BLUETOOTH = 9;
    public static final int BLUETOOTH_ADMIN = 8;

    public boolean checkPermissionForCamera() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionCall() {
        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return true;
        } else
            return false;
    }

    public boolean checkPermissionForWrite() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForStorage() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionGPS() {
        int fineLoc = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarseLoc = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (fineLoc == PackageManager.PERMISSION_GRANTED && coarseLoc == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionBluetooth() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionBluetoothAdmin() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_ADMIN)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForIMEI() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }


    public void requestPermissionForCamera() {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
    }

    public void requestPermissionFineLoc() {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                FINE_LOCATION_PERMISSION_REQUEST_CODE);
    }

    public void bluetooth() {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH},
                BLUETOOTH);
    }

    public void bluetoothAdmin() {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_ADMIN},
                BLUETOOTH_ADMIN);
    }

    public void requestPermissionCoarseLoc() {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                COARSE_LOCATION_PERMISSION_REQUEST_CODE);
    }

    public void requestPermissionForWrite() {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.
                WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_REQUEST_CODE);
    }

    public void requestPermissionCall() {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.
                CALL_PHONE}, CALL_REQUEST_CODE);
    }

    public void requestPermissionForStorage() {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                READ_EXTERNAL_STORAGE);
    }


    public boolean checkPermissionGetAccount() {
        int result = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.GET_ACCOUNTS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }


    public void requestPermissionGetIMEI() {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE}, IMEI_REQUEST_CODE);
    }

    public static boolean askForPermission(Activity activity, String permission, Integer requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    //This is called if user has denied the permissionCashier before
                    //In this case I am just asking the permissionCashier again
                    ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
                    return false;
                } else {
                    ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
                    return false;
                }
            } else {
                //Toast.makeText(activity, "" + permissionCashier + " is already granted.", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return true;
    }

}
