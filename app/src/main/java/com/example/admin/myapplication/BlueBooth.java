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
    public static String address = "00:0E:0E:0E:31:00";
    //public static UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static BluetoothSocket socket;
    public static OutputStream outputStream;
    public static InputStream inputStream;
    public static byte[] data = new byte[34];
    public static Thread threadSocket;
    public static final int COMMON = 1;
    public static boolean state = false;

    //震动时间长度;
    public static int vibrate = 40;

    public static Plane plane = new Plane();

    private static BasicActivity.onHandlerUser handlerUser;

    public static void setOnHandlerUser(BasicActivity.onHandlerUser hand) {
        handlerUser = hand;
    }

    //飞机的数据
    static class Plane {
        public int init_r = 1500;
        public int init_p = 1500;
        public int init_c = 1500;
        //油门
        public int power = 0;

        public int getPower() {
            return power;
        }

        public void setPower(int power) {
            this.power = power;

            if (handlerUser != null) {
                handlerUser.object = new int[]{getPower(), getRoll(), getCourse(), getPitching()};
                handlerUser.run();
            }
        }

        public int getRoll() {
            return roll;
        }

        public void setRoll(int roll) {
            this.roll = roll;
            if (handlerUser != null) {
                handlerUser.object = new int[]{getPower(), getRoll(), getCourse(), getPitching()};
                handlerUser.run();
            }
        }

        public int getCourse() {
            return course;
        }

        public void setCourse(int course) {
            this.course = course;

            if (handlerUser != null) {
                handlerUser.object = new int[]{getPower(), getRoll(), getCourse(), getPitching()};
                handlerUser.run();
            }
        }

        public int getPitching() {
            return pitching;
        }

        public void setPitching(int pitching) {
            this.pitching = pitching;

            if (handlerUser != null) {
                handlerUser.object = new int[]{getPower(), getRoll(), getCourse(), getPitching()};
                handlerUser.run();
            }
        }

        //航向
        public int roll = 1500;
        //横滚
        public int course = 1500;
        //俯仰
        public int pitching = 1500;


        //左航向的偏移度
        public int left_roll = 750;
        //右航向的偏移度
        public int right_roll = 2250;

        //左横滚的偏移度
        public int left_course = 750;
        //右横滚的偏移度
        public int right_course = 2250;

        //左俯仰的偏移度
        public int left_pitching = 750;
        //右俯仰的偏移度
        public int right_pitching = 2250;

    }

    public enum EnumPlaneData {
        left_roll,
        right_roll,
        left_course,
        right_course,
        left_pitching,
        right_pitching,
        roll,
        course,
        pitching
    }
}
