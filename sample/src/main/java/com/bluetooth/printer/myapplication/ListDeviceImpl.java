package com.bluetooth.printer.myapplication;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.printer.bluetooth.libsocketbluetoothprinter.lib.BitmapBluetooth;
import com.printer.bluetooth.libsocketbluetoothprinter.lib.BluetoothDiscover;
import com.printer.bluetooth.libsocketbluetoothprinter.lib.BtImage;
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

/*    private ArrayList<byte[]> makeList() {
        ArrayList<byte[]> list = new ArrayList<>();
        list.add(("\n").getBytes());
        list.add(("\n").getBytes());
        list.add(("\n").getBytes());
        list.add(("\n").getBytes());
        list.add(("\n").getBytes());
        list.add(("Test Print").getBytes());
        list.add(("\n").getBytes());
        list.add(("\n").getBytes());
        list.add(("\n").getBytes());
        list.add(("\n").getBytes());
        return list;
    }*/


    private ArrayList<byte[]> makeList() {
        ArrayList<byte[]> list = new ArrayList<>();
        Bitmap bitmapw = BitmapFactory.decodeResource(context.getResources(), R.drawable.deden2);
        Bitmap bitmap = Bitmap.createScaledBitmap(bitmapw, bitmapw.getWidth() / 5, bitmapw.getHeight() / 5, false);
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
            list.addAll(cutBitmap(1,bitmap,384));
        }
        return list;
    }

    public ArrayList<byte[]> cutBitmap(int h, Bitmap bitmapPrint, int w) {
        Bitmap bitmap = Bitmap.createScaledBitmap(bitmapPrint,
                bitmapPrint.getWidth(), bitmapPrint.getHeight(), false);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        boolean full = height % h == 0;
        int n = height % h == 0 ? height / h : height / h + 1;
        ArrayList<Bitmap> bitmaps = new ArrayList();
        ArrayList<byte[]> bytes = new ArrayList();

        for (int i = 0; i < n; ++i) {
            Bitmap b;
            if (full) {
                b = Bitmap.createBitmap(bitmap, 0, i * h, width, h);
            } else if (i == n - 1) {
                b = Bitmap.createBitmap(bitmap, 0, i * h, width, height - i * h);
            } else {
                b = Bitmap.createBitmap(bitmap, 0, i * h, width, h);
            }
            bitmaps.add(b);
        }

        for (Bitmap bm : bitmaps) {
            bytes.add(bitmapBluetoothDraw(w, bm));
        }
        return bytes;

    }

    private byte[] bitmapBluetoothDraw(int w, Bitmap bitmapPrint) {
        return BtImage.decodeBitmap(bitmapPrint);
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
