package com.bluetooth.printer.myapplication;

import android.bluetooth.BluetoothDevice;

public interface ListDevicePres {
    void init();

    void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults);

    void foundDevice(BluetoothDevice bluetoothDevice);

    void checkPair(int state, int prevState);

    void checkOn(int state);
}
