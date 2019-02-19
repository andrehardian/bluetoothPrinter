package com.bluetooth.printer.myapplication;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.printer.bluetooth.libsocketbluetoothprinter.R;
import com.printer.bluetooth.libsocketbluetoothprinter.lib.BitmapBluetooth;
import com.printer.bluetooth.libsocketbluetoothprinter.lib.BluetoothDiscover;
import com.printer.bluetooth.libsocketbluetoothprinter.lib.ConnectAndWrite;
import com.printer.bluetooth.libsocketbluetoothprinter.lib.Constant;
import com.printer.bluetooth.libsocketbluetoothprinter.lib.PermissionMarshmallow;
import com.printer.bluetooth.libsocketbluetoothprinter.lib.StarBitmap;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import lombok.Setter;

@EBean
public class ListDeviceImpl implements ListDevicePres, RecyclerListener {
    @RootContext
    protected Context context;
    @Bean
    protected BluetoothDiscover bluetoothDiscover;
    @Bean
    protected ConnectAndWrite connectAndWrite;
    @Bean
    protected PermissionMarshmallow permissionMarshmallow;
    private BluetoothDevice selectedDevice;

    @Setter
    private ListDeviceView listDeviceView;

    private ArrayList<BluetoothDevice> bluetoothDevices = new ArrayList<>();

    @Override
    public void init() {
        if (!permissionMarshmallow.checkPermissionBluetooth())
            permissionMarshmallow.bluetooth();
        if (!permissionMarshmallow.checkPermissionBluetoothAdmin())
            permissionMarshmallow.bluetoothAdmin();

        if (!permissionMarshmallow.checkPermissionGPS()) {
            permissionMarshmallow.requestPermissionFineLoc();
            permissionMarshmallow.requestPermissionCoarseLoc();
        } else {
            bluetoothDiscover.discover();
        }

    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == PermissionMarshmallow.COARSE_LOCATION_PERMISSION_REQUEST_CODE ||
                    requestCode == PermissionMarshmallow.FINE_LOCATION_PERMISSION_REQUEST_CODE) {
                bluetoothDiscover.discover();
            }
        }
    }

    @Override
    public void foundDevice(BluetoothDevice bluetoothDevice) {
        String c = bluetoothDevice.getName() + " - " + String.valueOf(bluetoothDevice.getBluetoothClass().getDeviceClass());
        if (bluetoothDevice.getBluetoothClass().getDeviceClass() == Constant.BLUETOOTH_PRINTER
                || bluetoothDevice.getBluetoothClass().getDeviceClass() == Constant.Bluetooth_INNER
                || bluetoothDevice.getBluetoothClass().getDeviceClass() == Constant.BLUETOOTH_PRINTER_2) {
            if (!bluetoothDevices.contains(bluetoothDevice)) {
                bluetoothDevices.add(bluetoothDevice);
                RecyclerView.Adapter adapter = listDeviceView.getAdapterListBluetooth
                        (bluetoothDevices, this);
                RecyclerView recyclerView = listDeviceView.getListBluetooth();
                if (recyclerView != null) {
                    if (recyclerView.getAdapter() != null) {
                        recyclerView.getAdapter().notifyDataSetChanged();
                    } else if (adapter != null) {
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        recyclerView.setAdapter(adapter);
                    }
                }
            }
        }
    }

    @Override
    public void checkPair(int state, int prevState) {
        if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
            connectAndWrite.printSingle(selectedDevice, "test \n coba \n");
        }
    }

    @Override
    public void checkOn(int state) {
        bluetoothDiscover.discover();
    }

    @Override
    public void onItemClick(Object o) {
        if (o instanceof BluetoothDevice) {
            selectedDevice = (BluetoothDevice) o;
            if (((BluetoothDevice) o).getBondState() == Constant.NOT_PAIR) {
                pair();
            } else {
                connectAndWrite.printMultiple(selectedDevice, makeList());
            }
        }
    }

    private ArrayList<byte[]> makeList() {
        ArrayList<byte[]> list = new ArrayList<>();
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.deden2);
        if (selectedDevice.getName() != null && selectedDevice.getName().toLowerCase().contains("star")) {
            list.add(new byte[]{0x1B, 0x2A, 33, (byte) 255, 3});
            if (bitmap != null)
                list.add(new StarBitmap(bitmap, false,
                        58)
                        .getImageRasterDataForPrinting_graphic(true));
            list.add(("ini test aja").getBytes());
            list.add(Constant.CUT_PAPER_STAR);
            list.add(Constant.KICK_DRAWER);

        } else {
            BitmapBluetooth bitmapBluetooth = new BitmapBluetooth();
            bitmapBluetooth.initCanvas(384);
            bitmapBluetooth.initPaint();
            try {
                bitmapBluetooth.canvas.drawBitmap(bitmapBluetooth.bitmapConvertMonochrome(bitmap)
                        , 0, 0, null);
                if (bitmapBluetooth.length < 0 + (float) bitmap.getHeight()) {
                    bitmapBluetooth.length = 0 + (float) bitmap.getHeight();
                }
            } catch (Exception var5) {
                var5.printStackTrace();
            }
            list.add(bitmapBluetooth.printDraw());
            list.add(("ini test aja").getBytes());
            list.add(("\n").getBytes());
            list.add(("\n").getBytes());
            list.add(("\n").getBytes());
        }
        return list;
    }


    private void pair() {
        try {
            Method method = selectedDevice.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(selectedDevice, (Object[]) null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notifyData(int position) {

    }
}
