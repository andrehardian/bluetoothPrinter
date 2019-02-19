package com.printer.bluetooth.libsocketbluetoothprinter.lib;

public class Constant {
//    intent request
    public static final int REQUEST_BLUETOOTH = 1;

//    status bluetooth
    public static final int BLUETOOTH_PRINTER = 1664;
    public static final int BLUETOOTH_PRINTER_2 = 7964;
    public static final int Bluetooth_INNER = 0;
    public static final int PAIRED = 12;
    public static final int NOT_PAIR = 10;


    //    cut paper
    public static final byte[] CUT_PAPER_STAR = new byte[]{27, 100, 3};
    public static final byte[] CUT_PAPER = new byte[]{(byte) 29, (byte) 86, (byte) 66, (byte) 0};

    //    kick drawe check in http://keyhut.com/popopen4.htm
    public static final byte[] KICK_DRAWER = new byte[]{27,7,11,55,7};
}
