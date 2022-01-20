package com.bluetooth.printer.myapplication;

import android.bluetooth.BluetoothDevice;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public interface ListDeviceView {
    RecyclerView getListBluetooth();
    RecyclerView.Adapter getAdapterListBluetooth(ArrayList<BluetoothDevice> bluetoothDevices, RecyclerListener listener);
}
