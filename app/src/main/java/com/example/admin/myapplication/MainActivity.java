package com.example.admin.myapplication;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;


public class MainActivity extends BasicActivity {

    //提示框
    private TextView tipe;
    //上下左右音效
    private MediaPlayer leftmediaPlayer;
    private MediaPlayer rightmediaPlayer;
    private MediaPlayer bottommediaPlayer;
    private MediaPlayer topmediaPlayer;

    //左边上升按钮
    private ImageButton goUp;
    //左边下降按钮
    private ImageButton goDown;
    //蓝牙按钮
    private Button bluebooth;
    //右边方向键
    private DirectionKey rightdirection;
    //油门
    private VerticalSeekBar verticalSeekBar;


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

        rightdirection = findViewById(R.id.rightDirection);
        goUp = findViewById(R.id.goup);
        goDown = findViewById(R.id.godown);
        verticalSeekBar=findViewById(R.id.verticalSeekBar);

        initView();
        initListener();
    }

    //定义监听回调
    private void initListener(){
        rightdirection.setOnShakeListener(new DirectionKey.OnShakeListener() {
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
        //设置消息接收函数
        setOnMessageCallback(new onMessageCallback() {
            @Override
            public void run(String msg) {
                tipe.setText(msg);
            }
        });


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


        verticalSeekBar.setOnSeekBarChangeListener(new VerticalSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(VerticalSeekBar VerticalBar, int progress, boolean fromUser) {
                //tipe.setText("油门值:"+progress);
            }

            @Override
            public void onStartTrackingTouch(VerticalSeekBar VerticalBar) {

            }

            @Override
            public void onStopTrackingTouch(VerticalSeekBar VerticalBar) {
                tipe.setText("油门值:"+VerticalBar.getProgress());
            }
        });
    }

    //初始化界面
    private void initView(){
        if (BlueBooth.state) tipe.setText("当前状态:已连接");
        else tipe.setText("当前状态:未连接");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (BlueBooth.socket != null) {
            try {
                BlueBooth.state = false;

                BlueBooth.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //连接蓝牙
    public void connectBlueTooth(View view) {
        showLoadingDialog("正在连接蓝牙....");
        blueBoothManager.connectBlueTooth(new BlueBoothManager.OnCallbackListener() {
            @Override
            public void onCallback(boolean state) {
               dismissLoadingDialog();
            }
        });
    }

    //返回首页
    public void backView(View view) {
        Intent intent = new Intent(MainActivity.this, StartUp.class);
        startActivity(intent);
    }

}

