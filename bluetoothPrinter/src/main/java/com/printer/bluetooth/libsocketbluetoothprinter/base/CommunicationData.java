package com.printer.bluetooth.libsocketbluetoothprinter.base;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by AndreHF on 4/11/2018.
 */

public class CommunicationData implements Observable.OnSubscribe<Object> {
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    @Getter
    private final Context context;
    private final ArrayList<byte[]> requestData;
    private final BluetoothDevice selectedDevice;
    private int delay = 200000;

    public CommunicationData(Context context, ArrayList<byte[]> requestData, BluetoothDevice selectedDevice) {
        this.context = context;
        this.requestData = requestData;
        this.selectedDevice = selectedDevice;
    }

    public CommunicationData setDelay(int delay){
        this.delay = delay;
        return this;
    }

    public CommunicationData(Context context, byte[] requestData, BluetoothDevice selectedDevice) {
        this.context = context;
        this.requestData = new ArrayList<>();
        this.requestData.add(requestData);
        this.selectedDevice = selectedDevice;
    }


    private void makeException(Subscriber<? super Object> subscriber, IOException e) {
        ExceptionCommunication exceptionCommunication = new ExceptionCommunication(e.getMessage(),
                selectedDevice.getName() + " - " + selectedDevice.getAddress(), this);
        subscriber.onError(exceptionCommunication);
    }

    @Override
    public void call(Subscriber<? super Object> subscriber) {
        OutputStream outputStream = null;
        InputStream inputStream = null;
        BluetoothSocket bluetoothSocket = null;
        try {
            bluetoothSocket = selectedDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            int testConnect = 0;
            while (!bluetoothSocket.isConnected() && testConnect < 100) {
                try {
                    testConnect++;
                    bluetoothSocket.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                    if (testConnect == 100) {
                        makeException(subscriber, e);
                    }
                }
            }
            if (bluetoothSocket.isConnected()) {
                inputStream = bluetoothSocket.getInputStream();
                outputStream = bluetoothSocket.getOutputStream();
                for (byte[] bytes : requestData) {
                    outputStream.write(bytes);
                    outputStream.flush();
                }
            }
            for (int pos = 0; pos < delay; pos++) {
                Log.i("delay","delay "+pos);
            }
        } catch (IOException e) {
            e.printStackTrace();
            makeException(subscriber, e);
        } finally {
            subscriber.onCompleted();
            try {
                if (bluetoothSocket != null) {
                    bluetoothSocket.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
