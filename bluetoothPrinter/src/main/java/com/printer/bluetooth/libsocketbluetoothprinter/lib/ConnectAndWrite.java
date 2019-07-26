package com.printer.bluetooth.libsocketbluetoothprinter.lib;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.ArrayList;

@EBean
public class ConnectAndWrite {
    @RootContext
    protected Context context;
    protected CommunicationManager communicationManager;

    @AfterInject
    protected void init(){
        communicationManager = new CommunicationManager(context);
    }

    public void printSingle(BluetoothDevice selectedDevice, String message) {
        communicationManager.print(selectedDevice, message.getBytes());
    }
    public void printMultiple(BluetoothDevice selectedDevice, ArrayList<byte[]> bytes) {
        communicationManager.printList(selectedDevice, bytes);
    }

}
