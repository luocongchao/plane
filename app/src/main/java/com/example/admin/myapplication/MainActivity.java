package com.example.admin.myapplication;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;



public class MainActivity extends AppCompatActivity {

    private TextView tipe;
    private MediaPlayer leftmediaPlayer;
    private MediaPlayer rightmediaPlayer;
    private MediaPlayer bottommediaPlayer;
    private MediaPlayer topmediaPlayer;

    private ImageButton goUp;
    private ImageButton goDown;
    private Button bluebooth;
    private ConstraintLayout constraintLayout;

    private BlueBoothManager blueBoothManager=new BlueBoothManager();


    private void dealDirection(DirectionKey.Direction direction) {

        switch (direction) {
            case DIRECTION_CENTER:
                tipe.setText("中心");
                break;
            case DIRECTION_DOWN:
                tipe.setText("下边");
                if (!bottommediaPlayer.isPlaying()) {
                    bottommediaPlayer.start();
                }
                break;
            case DIRECTION_DOWN_LEFT:
                tipe.setText("左下");
                break;
            case DIRECTION_DOWN_RIGHT:
                tipe.setText("右下");
                break;
            case DIRECTION_LEFT:
                tipe.setText("左边");
                if (!leftmediaPlayer.isPlaying()) {
                    leftmediaPlayer.start();
                }
                break;
            case DIRECTION_RIGHT:
                tipe.setText("右边");
                if (!rightmediaPlayer.isPlaying()) {
                    rightmediaPlayer.start();
                }
                break;
            case DIRECTION_UP_LEFT:
                tipe.setText("左上");
                break;
            case DIRECTION_UP_RIGHT:
                tipe.setText("右上");
                break;
            case DIRECTION_UP:
                tipe.setText("上面");
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

        bluebooth = findViewById(R.id.bluebooth);
        tipe = findViewById(R.id.tipe);
        constraintLayout = findViewById(R.id.activity_main);

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

        goUp = findViewById(R.id.goup);
        goDown = findViewById(R.id.godown);
        goUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:// 按下
                    case MotionEvent.ACTION_MOVE:// 移动
                        goUp.setBackground(getResources().getDrawable(R.drawable.left_up2));
                        blueBoothManager.sendData();
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

        blueBoothManager.setUpdateInvalidate(new BlueBoothManager.OnUpdateListener() {
            @Override
            public void onUpdateInvalidate(int i, String msg) {
                Message message = new Message();
                message.what = i;
                message.obj = msg;
                handler.sendMessage(message);
            }
        });

        if(BlueBooth.state) tipe.setText("当前状态:已连接");
        else  tipe.setText("当前状态:未连接");
    }



    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BlueBooth.CONNECT_BLUEBOOTH:
                    setTipe((String) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    public void setTipe(String msg) {
        tipe.setText(msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (BlueBooth.socket != null) {
            try {
                BlueBooth.state=false;
                BlueBooth.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void connectBlueTooth(View view) {
       blueBoothManager.connectBlueTooth();
    }

    public void backView(View view){
        Intent intent = new Intent(MainActivity.this, StartUp.class);
        startActivity(intent);
    }

}

