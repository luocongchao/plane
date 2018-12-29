package com.example.admin.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 *@ActionName:BlueBoothManager
 *@Descript: //TODO 蓝牙连接发送数据管理
 *@Author:lcc
 *@Date 2018/12/29  17:27
 *@Params
 *@Return
 *@Version 1.0.0
 **/
public class BlueBoothManager {
    private OnUpdateListener handle;


    /**
     *@ActionName:sendData
     *@Descript: //TODO 蓝牙发送数据
     *@Author:lcc
     *@Date 2018/12/29  17:28
     *@Params
     *@Return
     *@Version 1.0.0
     **/
    public void sendData() {
        if (BlueBooth.outputStream != null) {
            char[] bt = Protocol.WriteStatus((char) 500, (char) 0, (char) 0, (char) 0);
            for (int i = 0; i < bt.length; i++) {
                BlueBooth.data[i] = (byte) bt[i];
                System.out.println(BlueBooth.data[i] + "  ");
            }
            try {
                if(handle!=null) handle.onUpdateInvalidate(BlueBooth.CONNECT_BLUEBOOTH,"正在发送数据"+BlueBooth.data[0]);
                BlueBooth.outputStream.write(BlueBooth.data);
                if(handle!=null) handle.onUpdateInvalidate(BlueBooth.CONNECT_BLUEBOOTH,"数据发送成功");
            } catch (IOException e) {
                if(handle!=null) handle.onUpdateInvalidate(BlueBooth.CONNECT_BLUEBOOTH,"发送失败"+e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private OnCallbackListener callbackListener;
    /**
     *@ActionName:connectBlueTooth
     *@Descript: //TODO 连接蓝牙
     *@Author:lcc
     *@Date 2018/12/29  17:28
     *@Params
     *@Return
     *@Version 1.0.0
     **/
    public void connectBlueTooth() {
        BlueBooth.threadSocket = new Thread(new Connect());
        BlueBooth.threadSocket.start();
        callbackListener=null;
    }
    /**
     *@ActionName:connectBlueTooth
     *@Descript: //TODO 连接蓝牙
     *@Author:lcc
     *@Date 2018/12/29  17:28
     *@Params
     *@Return
     *@Version 1.0.0
     **/
    public void connectBlueTooth(OnCallbackListener onCallbackListener) {
        BlueBooth.threadSocket = new Thread(new Connect());
        BlueBooth.threadSocket.start();
        callbackListener=onCallbackListener;
    }
    public interface OnCallbackListener {
        void onCallback();
    }

    /**
     *@ActionName:setUpdateInvalidate
     *@Descript: //TODO 设置提示
     *@Author:lcc
     *@Date 2018/12/29  17:29
     *@Params
     *@Return
     *@Version 1.0.0
     **/
    public void setUpdateInvalidate(OnUpdateListener updateInvalidate) {
        this.handle = updateInvalidate;
    }

    public interface OnUpdateListener {
        void onUpdateInvalidate(int i, String msg);
    }

    private class Connect implements Runnable {
        /**
         *@ActionName:run
         *@Descript: //TODO 连接蓝牙的线程
         *@Author:lcc
         *@Date 2018/12/29  17:29
         *@Params
         *@Return
         *@Version 1.0.0
         **/
        @Override
        public void run() {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = adapter.getRemoteDevice(BlueBooth.address);
            if (device == null && handle != null) {
                handle.onUpdateInvalidate(BlueBooth.CONNECT_BLUEBOOTH, "连接蓝牙失败");
            }
            try {
                BlueBooth.socket = (BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(device, 1);
                //socket=device.createRfcommSocketToServiceRecord(uuid);
                if (handle != null) handle.onUpdateInvalidate(BlueBooth.CONNECT_BLUEBOOTH, "正在连接蓝牙");
                BlueBooth.socket.connect();
                BlueBooth.outputStream = BlueBooth.socket.getOutputStream();
                BlueBooth.inputStream = BlueBooth.socket.getInputStream();
                if (handle != null) handle.onUpdateInvalidate(BlueBooth.CONNECT_BLUEBOOTH, "蓝牙连接成功");
                BlueBooth.state=true;
            } catch (IOException e) {
                BlueBooth.state=false;
                if (handle != null) handle.onUpdateInvalidate(BlueBooth.CONNECT_BLUEBOOTH, "连接失败"+e.getMessage());
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                BlueBooth.state=false;
                if (handle != null) handle.onUpdateInvalidate(BlueBooth.CONNECT_BLUEBOOTH, "连接失败"+e.getMessage());
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                BlueBooth.state=false;
                if (handle != null) handle.onUpdateInvalidate(BlueBooth.CONNECT_BLUEBOOTH, "连接失败"+e.getMessage());
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                BlueBooth.state=false;
                if (handle != null) handle.onUpdateInvalidate(BlueBooth.CONNECT_BLUEBOOTH, "连接失败"+e.getMessage());
                e.printStackTrace();
            }
            if(callbackListener!=null) callbackListener.onCallback();
        }
    }
}
