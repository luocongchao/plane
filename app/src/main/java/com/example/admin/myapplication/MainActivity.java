package com.example.admin.myapplication;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
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
    private ImageButton goLeft;
    //左边下降按钮
    private ImageButton goRight;
    //蓝牙按钮
    private Button bluebooth;
    //右边方向键
    private DirectionKey rightdirection;
    //油门
    private VerticalSeekBar verticalSeekBar;

    //发送数据的开关
    private Switch aSwitch;

    //设置按钮
    private ImageButton settingBtn;

    //防止一直触发震动效果
    private boolean vibratorFlag=false;

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
        goLeft = findViewById(R.id.goleft);
        goRight = findViewById(R.id.goright);
        verticalSeekBar = findViewById(R.id.verticalSeekBar);
        aSwitch = findViewById(R.id.switch1);
        settingBtn=findViewById(R.id.main_setting);


        initView();
        initListener();
    }



    BlueBoothManager.OnConnectListener blueconnectlisten=new BlueBoothManager.OnConnectListener() {
        @Override
        public void onStartConnect() {
            userHandler(new onHandlerUser() {
                @Override
                void run() {
                    showLoadingDialog("正在连接蓝牙....");
                }
            });
        }

        @Override
        public void onMessage(String msg) {
            userHandler(msg, new onHandlerUser() {
                @Override
                void run() {
                    tipe.setText((String)this.object);
                }
            });
        }

        @Override
        public void onCallback(boolean state) {
            userHandler(new onHandlerUser() {
                @Override
                void run() {
                    dismissLoadingDialog();
                }
            });
        }
    };
    //定义监听回调
    private void initListener() {
        blueBoothManager.setConnectListener(blueconnectlisten);

        //设置方向摇杆的监听事件
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
                BlueBooth.plane.pitching = BlueBooth.plane.init;
                BlueBooth.plane.course = BlueBooth.plane.init;

            }
        });

        goLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:// 按下
                    case MotionEvent.ACTION_MOVE:// 移动
                        if(!vibratorFlag){
                            vibratorFlag=true;
                            vibrator.vibrate(BlueBooth.vibrate);
                            tipe.setText("左边");
                            if (!leftmediaPlayer.isPlaying()) {
                                leftmediaPlayer.start();
                            }
                        }
                        goLeft.setBackground(getResources().getDrawable(R.drawable.roll_left2));
                        BlueBooth.plane.roll = BlueBooth.plane.right_roll;
                        break;
                    case MotionEvent.ACTION_UP:// 抬起
                    case MotionEvent.ACTION_CANCEL:// 移出区域
                        vibratorFlag=false; //解除震动效果开关
                        goLeft.setBackground(getResources().getDrawable(R.drawable.roll_left));
                        BlueBooth.plane.roll = BlueBooth.plane.init;
                        break;
                }
                return true;
            }
        });
        goRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:// 按下
                    case MotionEvent.ACTION_MOVE:// 移动
                        if(!vibratorFlag){
                            vibratorFlag=true;
                            vibrator.vibrate(BlueBooth.vibrate);
                            tipe.setText("右边");
                            if (!rightmediaPlayer.isPlaying()) {
                                rightmediaPlayer.start();
                            }
                        }
                        goRight.setBackground(getResources().getDrawable(R.drawable.roll_right2));
                        BlueBooth.plane.roll = BlueBooth.plane.left_roll;
                        break;
                    case MotionEvent.ACTION_UP:// 抬起
                    case MotionEvent.ACTION_CANCEL:// 移出区域
                        vibratorFlag=false; //解除震动效果开关
                        goRight.setBackground(getResources().getDrawable(R.drawable.roll_right));
                        BlueBooth.plane.roll = BlueBooth.plane.init;
                        break;
                }
                return true;
            }
        });

        //实例化一个节流函数对象
        final Throttle throttle = new Throttle();
        //最快200毫秒调用一次
        throttle._during=200;
        //设置节流函数的回调函数
        throttle.setOnthrottleListener(new Throttle.onThrottle() {
            @Override
            public void run(int[] args) {
                //发送信息到handler对象 通知handler更新界面
                userHandler("油门值: " + String.valueOf(args[0]), new onHandlerUser() {
                    @Override
                    void run() {
                       tipe.setText((String)this.object);
                    }
                });
            }
        });
        verticalSeekBar.setOnSeekBarChangeListener(new VerticalSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(VerticalSeekBar VerticalBar, int progress, boolean fromUser) {

                BlueBooth.plane.power = progress * 10;
                //调用节流函数更新界面的通知栏(防止调用频率太高导致界面卡死)
                startThrottle(throttle, new int[]{progress * 10});
            }

            @Override
            public void onStartTrackingTouch(VerticalSeekBar VerticalBar) {

            }

            @Override
            public void onStopTrackingTouch(VerticalSeekBar VerticalBar) {

            }
        });

        //数据发送开关按钮的改变事件监听
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //开启发送数据的线程
                    threadPlane=new Thread(new WritePlane());
                    threadPlane.start();
                    System.gc();
                } else {
                    //关闭发送数据的线程
                    threadPlane.interrupt();
                }
            }
        });

        //设置按钮点击事件
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(BlueBooth.vibrate);
                showSettingDialog();
            }
        });


    }

    //初始化界面
    private void initView() {
        if (BlueBooth.state) tipe.setText("当前状态:已连接");
        else tipe.setText("当前状态:未连接");
    }



    //上升按钮
    public void onRise(View view) {
        vibrator.vibrate(BlueBooth.vibrate);
        verticalSeekBar.setProgress(75);
        BlueBooth.plane.power = 75 * 10;
        tipe.setText("油门值: " + 75*10);
    }
    //悬浮按钮
    public void onSuspension(View view) {
        vibrator.vibrate(BlueBooth.vibrate);
        verticalSeekBar.setProgress(50);
        BlueBooth.plane.power = 50 * 10;
        tipe.setText("油门值: " + 50*10);
    }
    //下降按钮
    public void onLand(View view) {
        vibrator.vibrate(BlueBooth.vibrate);
        verticalSeekBar.setProgress(25);
        BlueBooth.plane.power = 25 * 10;
        tipe.setText("油门值: " + 25*10);
    }


    //连接蓝牙
    public void connectBlueTooth(View view) {
        vibrator.vibrate(BlueBooth.vibrate);
        blueBoothManager.connectBlueTooth();
    }

    /**
    * @ActionName:backView
    * @Descript: //TODO 返回开始界面
    * @Params View
    * @Return null
    **/
    public void backView(View view) {
        vibrator.vibrate(BlueBooth.vibrate);
        //关闭发送数据的线程
        threadPlane.interrupt();
        Intent intent = new Intent(MainActivity.this, StartUp.class);
        startActivity(intent);
    }
    /**
    * @ActionName:dealDirection
    * @Descript: //TODO 方向摇杆的处理函数
    * @Params DirectionKey.Dire
    * @Return null
    **/
    private void dealDirection(DirectionKey.Direction direction) {
        if(direction!=DirectionKey.Direction.DIRECTION_CENTER)  vibrator.vibrate(BlueBooth.vibrate);
        switch (direction) {
            case DIRECTION_CENTER:
                tipe.setText("中心");
                BlueBooth.plane.pitching = BlueBooth.plane.init;
                BlueBooth.plane.course = BlueBooth.plane.init;
                break;

            case DIRECTION_DOWN:
                tipe.setText("下边");
                if (!bottommediaPlayer.isPlaying()) {
                    bottommediaPlayer.start();
                }
                BlueBooth.plane.pitching = BlueBooth.plane.left_pitching;
                BlueBooth.plane.course = BlueBooth.plane.init;
                break;
            case DIRECTION_UP:
                tipe.setText("上面");
                if (!topmediaPlayer.isPlaying()) {
                    topmediaPlayer.start();
                }
                BlueBooth.plane.pitching = BlueBooth.plane.right_pitching;
                BlueBooth.plane.course = BlueBooth.plane.init;
                break;

            case DIRECTION_DOWN_LEFT:
                tipe.setText("左下");
                BlueBooth.plane.pitching = BlueBooth.plane.left_pitching;
                BlueBooth.plane.course = BlueBooth.plane.right_course;
                break;
            case DIRECTION_DOWN_RIGHT:
                tipe.setText("右下");
                BlueBooth.plane.pitching = BlueBooth.plane.left_pitching;
                BlueBooth.plane.course = BlueBooth.plane.left_course;
                break;

            case DIRECTION_LEFT:

                BlueBooth.plane.pitching = BlueBooth.plane.init;
                BlueBooth.plane.course = BlueBooth.plane.right_course;
                break;
            case DIRECTION_RIGHT:

                BlueBooth.plane.pitching = BlueBooth.plane.init;
                BlueBooth.plane.course = BlueBooth.plane.left_course;
                break;
            case DIRECTION_UP_LEFT:
                tipe.setText("左上");
                BlueBooth.plane.pitching = BlueBooth.plane.right_pitching;
                BlueBooth.plane.course = BlueBooth.plane.right_course;
                break;
            case DIRECTION_UP_RIGHT:
                tipe.setText("右上");
                BlueBooth.plane.pitching =  BlueBooth.plane.right_pitching;
                BlueBooth.plane.course = BlueBooth.plane.left_course;
                break;

        }
    }


}

