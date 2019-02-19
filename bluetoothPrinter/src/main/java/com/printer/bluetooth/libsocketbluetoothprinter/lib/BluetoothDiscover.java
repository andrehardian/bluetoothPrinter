package com.printer.bluetooth.libsocketbluetoothprinter.lib;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.Set;

import lombok.Setter;

@EBean
public class BluetoothDiscover {
    @Setter
    @RootContext
    protected Activity activity;
    private BluetoothAdapter bluetoothAdapter;

    public Set<BluetoothDevice> discover() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBluetooth, Constant.REQUEST_BLUETOOTH);
        } else {
            if (bluetoothAdapter != null) {
                if (bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.cancelDiscovery();
                }
                bluetoothAdapter.startDiscovery();
                Set<BluetoothDevice> bluetoothDevices = bluetoothAdapter.getBondedDevices();
                return bluetoothDevices;
            }
        }
        return null;
    }
}
