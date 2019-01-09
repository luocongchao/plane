package com.example.admin.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;


/**
 * @ActionName:BlueBoothManager
 * @Descript: //TODO 蓝牙管理器
 * @Author:lcc
 * @Date 2018/12/29  17:27
 * @Params
 * @Return
 * @Version 1.0.0
 **/
public class BlueBoothManager {

    private static OnConnectListener connectListener;

    /**
     * @ActionName:sendData
     * @Descript: //TODO 蓝牙发送数据
     * @Author:lcc
     * @Date 2018/12/29  17:28
     * @Params
     * @Return
     * @Version 1.0.0
     **/
    public void sendData(byte[] data) {
        if (BlueBooth.outputStream != null) {
            try {
                BlueBooth.outputStream.write(data);
            } catch (IOException e) {
                if (connectListener != null) connectListener.onMessage("发送失败" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void setConnectListener(OnConnectListener listener){
        connectListener = listener;
    }

    /**
     * @ActionName:connectBlueTooth
     * @Descript: //TODO 连接蓝牙
     * @Author:lcc
     * @Date 2018/12/29  17:28
     * @Params
     * @Return
     * @Version 1.0.0
     **/
    public void connectBlueTooth() {
        BlueBooth.threadSocket = new Thread(new Connect());
        BlueBooth.threadSocket.start();
    }


    public interface OnConnectListener {
        void onCallback(boolean state);

        void onStartConnect();

        void onMessage(String msg);
    }

    public BroadcastReceiver stateChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_ACL_CONNECTED == action) {
                if (connectListener != null) {
                    connectListener.onMessage("蓝牙连接成功!");
                }
                BlueBooth.state = true;
            }
            if (BluetoothDevice.ACTION_ACL_DISCONNECTED == action) {
                if (connectListener != null) {
                    connectListener.onMessage("蓝牙连接已断开!");
                }
                BlueBooth.state = false;
            }
            if (BluetoothAdapter.ACTION_STATE_CHANGED == action) {
                if (connectListener != null) {
                    connectListener.onMessage("蓝牙已关闭!");
                }
                BlueBooth.state = false;
            }
        }
    };


    private class Connect implements Runnable {
        /**
         * @ActionName:run
         * @Descript: //TODO 连接蓝牙的线程
         * @Author:lcc
         * @Date 2018/12/29  17:29
         * @Params
         * @Return
         * @Version 1.0.0
         **/
        @Override
        public void run() {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            if (adapter == null) {
                if (connectListener != null) {
                    connectListener.onMessage("找不到蓝牙设备");
                    connectListener.onCallback(false);
                }
                return;
            }
            //判断蓝牙是否打开
            if (!adapter.isEnabled()) {
                //申请打开蓝牙
                adapter.enable();
                int i = 0;
                //等待蓝牙打开
                while (!adapter.isEnabled()) {
                    try {
                        i++;
                        Thread.sleep(100);
                        if (i > 30) break;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            BluetoothDevice device = adapter.getRemoteDevice(BlueBooth.address);
            try {
                if (connectListener != null) connectListener.onStartConnect();
                BlueBooth.socket = (BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(device, 1);
                //socket=device.createRfcommSocketToServiceRecord(uuid);
                if (connectListener != null) connectListener.onMessage("正在连接蓝牙");
                BlueBooth.socket.connect();
                BlueBooth.outputStream = BlueBooth.socket.getOutputStream();
                BlueBooth.inputStream = BlueBooth.socket.getInputStream();
                BlueBooth.state = true;
                if (connectListener != null) connectListener.onCallback(true);

            } catch (IOException e) {
                handleDeal(e.getMessage());
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                handleDeal(e.getMessage());
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                handleDeal(e.getMessage() == null ? " 蓝牙未打开" : e.getMessage());
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                handleDeal(e.getMessage());
                e.printStackTrace();
            }

        }

        private void handleDeal(String msg) {
            BlueBooth.state = false;
            if (connectListener != null) {
                connectListener.onMessage("连接失败:" + msg);
                connectListener.onCallback(false);
            }
        }
    }


}
