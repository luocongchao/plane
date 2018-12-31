package com.example.admin.myapplication;

import android.bluetooth.BluetoothSocket;

import java.io.InputStream;
import java.io.OutputStream;


/**
 *@ActionName:BlueBooth
 *@Descript: //TODO 蓝牙对象的初始化数据
 *@Author:lcc
 *@Date 2018/12/29  17:27
 *@Params
 *@Return
 *@Version 1.0.0
 **/
public class BlueBooth {
    public static String address = "1C:5C:F2:73:FC:C2";
    //public static UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static BluetoothSocket socket;
    public static OutputStream outputStream;
    public static InputStream inputStream;
    public static byte[] data = new byte[34];
    public static Thread threadSocket;
    public static final int CONNECT_BLUEBOOTH = 1;
    public static boolean state = false;
}
