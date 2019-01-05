package com.example.admin.myapplication;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;

import java.io.InputStream;
import java.io.OutputStream;


/**
 * @ActionName:BlueBooth
 * @Descript: //TODO 蓝牙对象的初始化数据
 * @Author:lcc
 * @Date 2018/12/29  17:27
 * @Params
 * @Return
 * @Version 1.0.0
 **/
public class BlueBooth {
    public static String address = "00:0E:0E:15:84:F2";
    //public static UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static BluetoothSocket socket;
    public static OutputStream outputStream;
    public static InputStream inputStream;
    public static byte[] data = new byte[34];
    public static Thread threadSocket;
    public static final int CONNECT_BLUEBOOTH = 1;
    public static final int SETTING_TXT = 2;
    public static boolean state = false;

    //震动时间长度;
    public static int vibrate=40;

    //飞机的数据
    static class plane {
        //
        public static int init=1500;
        //油门
        public static int power = 0;
        //航向
        public static int roll = init;
        //横滚
        public static int course = init;
        //俯仰
        public static int pitching = init;


        //左航向的偏移度
        public static int left_roll = init;
        //右航向的偏移度
        public static int right_roll = init;

        //左横滚的偏移度
        public static int left_course = init;
        //右横滚的偏移度
        public static int right_course = init;

        //左俯仰的偏移度
        public static int left_pitching = init;
        //右俯仰的偏移度
        public static int right_pitching = init;

    }

    public enum EnumPlaneData {
        left_roll,
        right_roll,
        left_course,
        right_course,
        left_pitching,
        right_pitching
    }
}
