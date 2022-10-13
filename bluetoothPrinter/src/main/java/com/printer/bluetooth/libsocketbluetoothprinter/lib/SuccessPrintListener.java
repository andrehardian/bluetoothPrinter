package com.printer.bluetooth.libsocketbluetoothprinter.lib;

import android.bluetooth.BluetoothDevice;

public interface SuccessPrintListener {
    void successPrint(BluetoothDevice bluetoothDevice);
}
