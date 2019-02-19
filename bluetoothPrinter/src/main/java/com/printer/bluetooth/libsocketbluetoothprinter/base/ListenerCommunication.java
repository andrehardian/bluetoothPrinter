package com.printer.bluetooth.libsocketbluetoothprinter.base;

/**
 * Created by AndreHF on 4/11/2018.
 */

public interface ListenerCommunication {
    void success(Object o);
    void failCommunication(ExceptionCommunication exceptionCommunication);
}
