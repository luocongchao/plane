package com.example.admin.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;



public class MainActivity extends AppCompatActivity {

    private String address = "1C:5C:F2:73:FC:C2";
    private UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private byte[] data = new byte[34];
    private Thread threadSocket;

    private TextView textView;
    private MediaPlayer leftmediaPlayer;
    private MediaPlayer rightmediaPlayer;
    private MediaPlayer bottommediaPlayer;
    private MediaPlayer topmediaPlayer;

    private ImageButton goUp;
    private ImageButton goDown;



    private void dealDirection(DirectionKey.Direction direction) {

        switch (direction) {
            case DIRECTION_CENTER:
                textView.setText("中心");
                break;
            case DIRECTION_DOWN:
                textView.setText("下边");
                if (!bottommediaPlayer.isPlaying()) {
                    bottommediaPlayer.start();
                }
                break;
            case DIRECTION_DOWN_LEFT:
                textView.setText("左下");
                break;
            case DIRECTION_DOWN_RIGHT:
                textView.setText("右下");
                break;
            case DIRECTION_LEFT:
                textView.setText("左边");
                if (!leftmediaPlayer.isPlaying()) {
                    leftmediaPlayer.start();
                }
                break;
            case DIRECTION_RIGHT:
                textView.setText("右边");
                if (!rightmediaPlayer.isPlaying()) {
                    rightmediaPlayer.start();
                }
                break;
            case DIRECTION_UP_LEFT:
                textView.setText("左上");
                break;
            case DIRECTION_UP_RIGHT:
                textView.setText("右上");
                break;
            case DIRECTION_UP:
                textView.setText("上面");
                if (!topmediaPlayer.isPlaying()) {
                    topmediaPlayer.start();
                }
                break;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        leftmediaPlayer = MediaPlayer.create(this, R.raw.zuo);
        topmediaPlayer = MediaPlayer.create(this, R.raw.qian);
        rightmediaPlayer = MediaPlayer.create(this, R.raw.you);
        bottommediaPlayer = MediaPlayer.create(this, R.raw.hou);


        textView = findViewById(R.id.tipe);
        DirectionKey leftdirection = findViewById(R.id.rightDirection);
        leftdirection.setOnShakeListener(new DirectionKey.OnShakeListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void direction(DirectionKey.Direction direction) {
                dealDirection(direction);
            }

            @Override
            public void onFinish() {

            }
        });

        goUp=findViewById(R.id.goup);
        goDown=findViewById(R.id.godown);
        goUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:// 按下
                    case MotionEvent.ACTION_MOVE:// 移动
                        goUp.setBackground(getResources().getDrawable(R.drawable.left_up2));
                        break;
                    case MotionEvent.ACTION_UP:// 抬起
                    case MotionEvent.ACTION_CANCEL:// 移出区域
                        goUp.setBackground(getResources().getDrawable(R.drawable.left_up));
                        break;
                }
                return true;
            }
        });
        goDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:// 按下
                    case MotionEvent.ACTION_MOVE:// 移动
                        goDown.setBackground(getResources().getDrawable(R.drawable.left_down2));
                        break;
                    case MotionEvent.ACTION_UP:// 抬起
                    case MotionEvent.ACTION_CANCEL:// 移出区域
                        goDown.setBackground(getResources().getDrawable(R.drawable.left_down));
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendData() throws IOException {
        if (outputStream != null) {
            outputStream.write(data);
        }
    }

    public void connectBlueTooth(View view) {
        threadSocket = new Thread(new Connect());
        threadSocket.start();
    }

    private class Connect implements Runnable {
        @Override
        public void run() {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = adapter.getRemoteDevice(address);
            if (device == null) {
                textView.setText("获取失败");
            }
            try {
                socket = (BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(device, 1);
                //socket=device.createRfcommSocketToServiceRecord(uuid);
                socket.connect();
                outputStream = socket.getOutputStream();
                inputStream = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }
}

