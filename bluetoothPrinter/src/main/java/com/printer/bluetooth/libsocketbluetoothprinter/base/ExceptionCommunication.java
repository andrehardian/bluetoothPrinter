package com.printer.bluetooth.libsocketbluetoothprinter.base;

import android.os.Build;
import androidx.annotation.RequiresApi;

import lombok.Getter;

/**
 * Created by AndreHF on 4/12/2018.
 */

public class ExceptionCommunication extends RuntimeException {
    @Getter
    private String message;
    @Getter
    private String address;

    public ExceptionCommunication(String message, String address, CommunicationData communicationData) {
        super(message);
        this.address = address;
        this.message = message;
        this.communicationData = communicationData;
    }

    public ExceptionCommunication(String message, Throwable cause, String address, CommunicationData communicationData) {
        super(message, cause);
        this.message = message;
        this.address = address;
        this.communicationData = communicationData;
    }

    public ExceptionCommunication(Throwable cause, String address, CommunicationData communicationData) {
        super(cause);
        this.address = address;
        this.communicationData = communicationData;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ExceptionCommunication(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String address, CommunicationData communicationData) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.message = message;
        this.address = address;
        this.communicationData = communicationData;
    }

    @Getter
    private CommunicationData communicationData;

}
