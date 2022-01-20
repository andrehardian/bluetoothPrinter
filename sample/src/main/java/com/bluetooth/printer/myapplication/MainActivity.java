package com.bluetooth.printer.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity implements ListDeviceView {

    @Bean
    protected ListDeviceImpl listDevice;
    @ViewById
    protected RecyclerView test;

    @AfterViews
    protected void init() {
        listDevice.setListDeviceView(this);
        listDevice.init();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        listDevice.onRequestPermissionResult(requestCode, permissions, grantResults);
    }

    @Receiver(actions = BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
    protected void finishDiscover() {
        Toast.makeText(this, "finish discover", Toast.LENGTH_SHORT).show();
    }

    @Receiver(actions = BluetoothDevice.ACTION_FOUND)
    protected void foundDevice(@Receiver.Extra(BluetoothDevice.EXTRA_DEVICE)
                                       BluetoothDevice bluetoothDevice) {
        listDevice.foundDevice(bluetoothDevice);
    }

    @Receiver(actions = BluetoothDevice.ACTION_BOND_STATE_CHANGED)
    protected void pair(@Receiver.Extra(BluetoothDevice.EXTRA_BOND_STATE)
                                int state,
                        @Receiver.Extra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE)
                                int prevState) {
        listDevice.checkPair(state, prevState);
    }

    @Receiver(actions = BluetoothAdapter.ACTION_STATE_CHANGED)
    protected void onOffBluetooth(@Receiver.Extra(BluetoothAdapter.EXTRA_STATE)
                                          int state) {
        listDevice.checkOn(state);
    }

    @Override
    public RecyclerView getListBluetooth() {
        return test;
    }

    @Override
    public RecyclerView.Adapter getAdapterListBluetooth(ArrayList<BluetoothDevice>
                                                                bluetoothDevices, RecyclerListener listener) {
        return new AdapteraSample(bluetoothDevices, listener);
    }
}
