package com.printer.bluetooth.libsocketbluetoothprinter.lib;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.printer.bluetooth.libsocketbluetoothprinter.base.CallBackSubscriber;
import com.printer.bluetooth.libsocketbluetoothprinter.base.CommunicationData;
import com.printer.bluetooth.libsocketbluetoothprinter.base.ExceptionCommunication;
import com.printer.bluetooth.libsocketbluetoothprinter.base.ListenerCommunication;
import com.printer.bluetooth.libsocketbluetoothprinter.base.SubScriberCommunication;

import java.util.ArrayList;

import lombok.Setter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CommunicationManager implements CallBackSubscriber, ListenerCommunication {
    protected final Context context;
    protected DialogManager dialogManager;
    private ProgressDialog progressDialog;
    private ListenerCommunication listener;
    private int totalRequest;
    @Setter
    private MutableLiveData<Boolean> finishListener;
    @Setter
    private SuccessPrintListener successListener;

    public CommunicationManager(Context context) {
        this.context = context;
        dialogManager = new DialogManager(context);
    }

    @Override
    public void onServiceFinish() {
        if (progressDialog != null && progressDialog.isShowing() && totalRequest == 1) {
            try {
                progressDialog.dismiss();
                if (finishListener != null)
                    finishListener.postValue(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (totalRequest > 0)
            totalRequest -= 1;

    }

    @Override
    public void success(Object o) {
        if (successListener!=null){
            successListener.successPrint((BluetoothDevice) o);
        }
    }

    @Override
    public void failCommunication(ExceptionCommunication exceptionCommunication) {
        String message = exceptionCommunication.getMessage();
        message = message.equalsIgnoreCase("read failed, socket might closed or timeout, read ret: -1")?"Pastikan printer dalam kondisi standby":message;
        dialogManager.errorDialog(message);
        if (finishListener != null)
            finishListener.postValue(true);
    }

    protected void subscribe(CommunicationData communicationData) {
        try {
            if (progressDialog == null && context != null) {
                progressDialog = new ProgressDialog(context);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("waiting");
            }
            if (!progressDialog.isShowing()) {
                progressDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        totalRequest++;
        Observable.create(communicationData).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.newThread())
                .subscribe(new SubScriberCommunication().setListener(this).setCallBackSubscriber(this));
    }

    public void print(BluetoothDevice bluetoothDevice, byte[] message) {
        subscribe(new CommunicationData(context, message, bluetoothDevice));
    }


    public void printList(BluetoothDevice bluetoothDevice, ArrayList<byte[]> messages) {
        subscribe(new CommunicationData(context, messages, bluetoothDevice));
    }

    public void print(BluetoothDevice bluetoothDevice, byte[] message, int delay) {
        subscribe(new CommunicationData(context, message, bluetoothDevice).setDelay(delay));
    }

    public void printList(BluetoothDevice bluetoothDevice, ArrayList<byte[]> messages, int delay) {
        subscribe(new CommunicationData(context, messages, bluetoothDevice).setDelay(delay));
    }

}
