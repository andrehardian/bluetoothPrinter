package com.printer.bluetooth.libsocketbluetoothprinter.base;


import rx.Subscriber;

/**
 * Created by AndreHF on 4/11/2018.
 */

public class SubScriberCommunication<T> extends Subscriber<T> {
    private CallBackSubscriber callBackSubscriber;

    private ListenerCommunication listener;

    public SubScriberCommunication<T> setListener(ListenerCommunication listener) {
        this.listener = listener;
        return this;
    }


    public SubScriberCommunication<T> setCallBackSubscriber(CallBackSubscriber callBackSubscriber) {
        this.callBackSubscriber = callBackSubscriber;
        return this;
    }

    @Override
    public void onCompleted() {
        callBackSubscriber.onServiceFinish();
        unsubscribe();
    }

    @Override
    public void onError(Throwable e) {
        callBackSubscriber.onServiceFinish();
        if (e instanceof ExceptionCommunication) {
            listener.failCommunication((ExceptionCommunication) e);
        }
    }

    @Override
    public void onNext(T response) {
        callBackSubscriber.onServiceFinish();
        if (response != null)
            listener.success(response);
    }
}
