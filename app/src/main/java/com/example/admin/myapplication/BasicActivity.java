package com.example.admin.myapplication;


import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class BasicActivity extends AppCompatActivity {
    //定时任务接口
    protected interface onTimeout {
        void run();
    }

    //消息回调接口
    protected interface onMessageCallback {
        void run(String msg);
    }

    //loading对象对话框
    private AlertDialog alertDialog;

    //震动
    Vibrator vibrator;


    //蓝牙管理对象
    protected BlueBoothManager blueBoothManager = new BlueBoothManager();
    //消息处理对象
    protected onMessageCallback messageCallback;

    //当前handler对象
    protected static Handler handler;

    //设置消息处理对象
    protected void setOnMessageCallback(onMessageCallback onMessageCallback) {
        this.messageCallback = onMessageCallback;
    }

    /**
     * @ActionName:getMessage
     * @Descript: //TODO 获取一个消息对象
     * @Params i消息的类型
     * @Params msg消息参数值
     * @Return Message
     **/
    protected Message getMessage(int i, String msg) {
        Message message = new Message();
        message.what = i;
        message.obj = msg;
        return message;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        handler = new Handler() {
            //重写当前handler的消息接收处理函数
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case BlueBooth.CONNECT_BLUEBOOTH:
                        //运行消息处理对象的run函数
                        if (messageCallback != null) messageCallback.run((String) msg.obj);
                        break;
                    case BlueBooth.SETTING_TXT:
                        //运行消息处理对象的run函数
                        SETTING_TXT_MessageBack((String) msg.obj);
                        break;
                    default:
                        break;
                }
            }
        };
        //设置蓝牙管理器的消息通知
        blueBoothManager.setUpdateInvalidate(new BlueBoothManager.OnUpdateListener() {
            @Override
            public void onUpdateInvalidate(int i, String msg) {
                handler.sendMessage(getMessage(i, msg));
            }
        });
        //震动对象
        vibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }


    //弹出loading框
    protected void showLoadingDialog(String msg) {
        showLoadingDialog();
        TextView textView = alertDialog.findViewById(R.id.dialog_txt);
        textView.setText(msg);
    }


    //弹出loading框
    protected void showLoadingDialog() {
        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
        alertDialog.setCancelable(false);
        //dialog点击的监听事件
        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_BACK)
                    return true;
                return false;
            }
        });
        alertDialog.show();
        alertDialog.setContentView(R.layout.layout_alert);
        alertDialog.setCanceledOnTouchOutside(false);
        TextView textView = alertDialog.findViewById(R.id.dialog_txt);
        textView.setText("loading...");
    }

    //隐藏loading框
    protected void dismissLoadingDialog() {
        if (null != alertDialog && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    //根据时间隐藏loading框
    protected void dismissLoadingDialog(int delay) {
        setTimeOut(new onTimeout() {
            @Override
            public void run() {
                dismissLoadingDialog();
            }
        }, delay);
    }

    // 设定指定任务task在指定时间time执行 schedule(TimerTask task, Date time)
    protected void setTimeOut(final onTimeout onTimeout, int delay) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                if (onTimeout != null) {
                    onTimeout.run();
                }
            }
        }, delay);// 设定指定的时间time,此处为3000毫秒
    }

    //定时发送数据线程对象
    public Thread threadPlane = new Thread(new WritePlane());

    //定时发送数据线程
    public class WritePlane implements Runnable {
        @Override
        public void run() {
            while (true) {
                char[] chars = Protocol.WriteStatus((char) BlueBooth.plane.power, (char) BlueBooth.plane.roll, (char) BlueBooth.plane.course, (char) BlueBooth.plane.pitching);
                byte[] bytes = Protocol.charToByteArray(chars);
                blueBoothManager.sendData(bytes);
                System.out.println("调用发送数据！！！ 油门：" + BlueBooth.plane.power + " 航向：" + BlueBooth.plane.roll + " 横滚：" + BlueBooth.plane.course + " 俯仰：" + BlueBooth.plane.pitching);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                    break;
                }
            }
        }
    }

    /*
     * 节流函数对象
     * */
    public static class Throttle {
        //当前时间
        Date _cur = new Date();
        //开始时间
        Date _prev = new Date();
        //函数调用频率  默认最快100毫秒调用一次
        int _during = 100;
        //定时器
        Timer _timer = new Timer();
        //是否是第一次调用
        boolean _isFirst = true;

        //回调函数接口
        interface onThrottle {
            void run(int[] args);
        }

        //设置节流函数的回调函数
        void setOnthrottleListener(onThrottle _throttle) {
            this._throttle = _throttle;
        }

        //回调函数对象
        private onThrottle _throttle;

    }

    /**
     * @ActionName:startThrottle
     * @Descript: //TODO 开始一个节流函数
     * @Params Throttle thr//节流函数对象
     * @Params String[] args参数值
     * @Return null
     **/
    protected void startThrottle(final Throttle thr, final int[] args) {
        thr._cur = new Date();
        //判断此次调用是否为第一次调用 或者当前的时间减去上次调用的时间是否大于用户设置的频率时间
        if (thr._isFirst || thr._cur.getTime() - thr._prev.getTime() >= thr._during) {
            thr._prev = thr._cur;  //修改上次的调用时间为当前时间
            thr._isFirst = false;    //修改当前不是第一次调用
            thr._throttle.run(args);   //运行用户定义的接口

        } else {
            thr._timer.cancel();
            thr._timer = new Timer();
            thr._timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    thr._isFirst = true;  //修改当前是第一次调用
                    thr._throttle.run(args); //运行用户定义的接口
                }
            }, thr._during);// 设定指定的时间time,此处为3000毫秒
        }

    }


    //--------------------------------------------------设置界面--------------------------------------//
    //setting对象对话框
    private AlertDialog settingDialog;
    //设置关闭按钮
    private Button settingBackBtn;

    //设置关闭按钮
    private Button left_roll_add;
    //设置关闭按钮
    private Button left_roll_reduce;
    //设置关闭按钮
    private Button right_roll_add;
    //设置关闭按钮
    private Button right_roll_reduce;
    //设置关闭按钮
    private Button left_course_add;
    //设置关闭按钮
    private Button left_course_reduce;
    //设置关闭按钮
    private Button right_course_add;
    //设置关闭按钮
    private Button right_course_reduce;
    //设置关闭按钮
    private Button left_pitching_add;
    //设置关闭按钮
    private Button left_pitching_reduce;
    //设置关闭按钮
    private Button right_pitching_add;
    //设置关闭按钮
    private Button right_pitching_reduce;

    //设置关闭按钮
    private TextView left_roll_txt;
    //设置关闭按钮
    private TextView left_course_txt;
    //设置关闭按钮
    private TextView left_pitching_txt;
    //设置关闭按钮
    private TextView right_roll_txt;
    //设置关闭按钮
    private TextView right_course_txt;
    //设置关闭按钮
    private TextView right_pitching_txt;

    //临时需改变的文本框
    private TextView tempTxt;
    private boolean add_reduce_state;
    private int tempMaxValue;
    private int tempMinValue;
    private boolean tempThreadFlag = false;
    private BlueBooth.EnumPlaneData planeData;


    //计算长按加减号的子线程
    private Thread threadSetting = new Thread(new settingTouch());

    private class settingTouch implements Runnable {
        @Override
        public void run() {
            int sleepnum = 100;
            while (true) {
                int num = Integer.parseInt(tempTxt.getText().toString());
                if (add_reduce_state) {
                    if (num < tempMaxValue) {
                        num += 5;
                    }
                } else {
                    if (num > tempMinValue) {
                        num -= 5;
                    }
                }
                switch (planeData) {
                    case left_roll:
                        BlueBooth.plane.left_roll = num;
                        break;
                    case left_course:
                        BlueBooth.plane.left_course = num;
                        break;
                    case left_pitching:
                        BlueBooth.plane.left_pitching = num;
                        break;
                    case right_course:
                        BlueBooth.plane.right_course = num;
                        break;
                    case right_pitching:
                        BlueBooth.plane.right_pitching = num;
                        break;
                    case right_roll:
                        BlueBooth.plane.right_roll = num;
                        break;
                }
                handler.sendMessage(getMessage(BlueBooth.SETTING_TXT, String.valueOf(num)));
                try {
                    Thread.sleep(sleepnum);
                    if (sleepnum > 20) {
                        sleepnum -= 5;
                    }
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                    break;
                }
            }
        }
    }

    //SETTING_TXT消息类型的回调函数
    private void SETTING_TXT_MessageBack(String txt) {
        tempTxt.setText(txt);
    }

    private void TouchDeal(MotionEvent event, boolean add_reduce) {
        add_reduce_state = add_reduce;
        try {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:// 按下
                case MotionEvent.ACTION_MOVE:// 移动
                    //threadSetting.interrupt();
                    if (!tempThreadFlag) {
                        tempThreadFlag = true;
                        vibrator.vibrate(BlueBooth.vibrate); //震动一下
                        threadSetting = new Thread(new settingTouch());
                        threadSetting.start();
                        System.gc();
                    }
                    break;
                case MotionEvent.ACTION_UP:// 抬起
                case MotionEvent.ACTION_CANCEL:// 移出区域
                    tempThreadFlag = false;
                    threadSetting.interrupt();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initSetting() {
        settingBackBtn = settingDialog.findViewById(R.id.setting_back);
        //设置界面的返回按钮
        settingBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(BlueBooth.vibrate);
                dismissSettingDialog();
            }
        });
        left_roll_add = settingDialog.findViewById(R.id.left_roll_add);
        left_roll_reduce = settingDialog.findViewById(R.id.left_roll_reduce);
        left_course_add = settingDialog.findViewById(R.id.left_course_add);
        left_course_reduce = settingDialog.findViewById(R.id.left_course_reduce);
        left_pitching_add = settingDialog.findViewById(R.id.left_pitching_add);
        left_pitching_reduce = settingDialog.findViewById(R.id.left_pitching_reduce);

        right_roll_add = settingDialog.findViewById(R.id.right_roll_add);
        right_roll_reduce = settingDialog.findViewById(R.id.right_roll_reduce);
        right_course_add = settingDialog.findViewById(R.id.right_course_add);
        right_course_reduce = settingDialog.findViewById(R.id.right_course_reduce);
        right_pitching_add = settingDialog.findViewById(R.id.right_pitching_add);
        right_pitching_reduce = settingDialog.findViewById(R.id.right_pitching_reduce);

        left_roll_txt = settingDialog.findViewById(R.id.left_roll_txt);
        left_course_txt = settingDialog.findViewById(R.id.left_course_txt);
        left_pitching_txt = settingDialog.findViewById(R.id.left_pitching_txt);
        right_roll_txt = settingDialog.findViewById(R.id.right_roll_txt);
        right_course_txt = settingDialog.findViewById(R.id.right_course_txt);
        right_pitching_txt = settingDialog.findViewById(R.id.right_pitching_txt);
        left_roll_txt.setText(String.valueOf(BlueBooth.plane.left_roll));
        left_course_txt.setText(String.valueOf(BlueBooth.plane.left_course));
        left_pitching_txt.setText(String.valueOf(BlueBooth.plane.left_pitching));
        right_roll_txt.setText(String.valueOf(BlueBooth.plane.right_roll));
        right_course_txt.setText(String.valueOf(BlueBooth.plane.right_course));
        right_pitching_txt.setText(String.valueOf(BlueBooth.plane.right_pitching));

        left_roll_add.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tempTxt = left_roll_txt;
                tempMaxValue = 1500;
                tempMinValue = 0;
                planeData = BlueBooth.EnumPlaneData.left_roll;
                TouchDeal(event, true);
                return true;
            }
        });
        left_roll_reduce.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tempTxt = left_roll_txt;
                tempMaxValue = 1500;
                tempMinValue = 0;
                planeData = BlueBooth.EnumPlaneData.left_roll;
                TouchDeal(event, false);
                return true;
            }
        });

        right_roll_add.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tempTxt = right_roll_txt;
                tempMaxValue = 3000;
                tempMinValue = 1500;
                planeData = BlueBooth.EnumPlaneData.right_roll;
                TouchDeal(event, true);
                return true;
            }
        });
        right_roll_reduce.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tempTxt = right_roll_txt;
                tempMaxValue = 3000;
                tempMinValue = 1500;
                planeData = BlueBooth.EnumPlaneData.right_roll;
                TouchDeal(event, false);
                return true;
            }
        });


        left_course_add.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tempTxt = left_course_txt;
                tempMaxValue = 1500;
                tempMinValue = 0;
                planeData = BlueBooth.EnumPlaneData.left_course;
                TouchDeal(event, true);
                return true;
            }
        });
        left_course_reduce.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tempTxt = left_course_txt;
                tempMaxValue = 1500;
                tempMinValue = 0;
                planeData = BlueBooth.EnumPlaneData.left_course;
                TouchDeal(event, false);
                return true;
            }
        });

        right_course_add.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tempTxt = right_course_txt;
                tempMaxValue = 3000;
                tempMinValue = 1500;
                planeData = BlueBooth.EnumPlaneData.right_course;
                TouchDeal(event, true);
                return true;
            }
        });
        right_course_reduce.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tempTxt = right_course_txt;
                tempMaxValue = 3000;
                tempMinValue = 1500;
                planeData = BlueBooth.EnumPlaneData.right_course;
                TouchDeal(event, false);
                return true;
            }
        });


        left_pitching_add.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tempTxt = left_pitching_txt;
                tempMaxValue = 1500;
                tempMinValue = 0;
                planeData = BlueBooth.EnumPlaneData.left_pitching;
                TouchDeal(event, true);
                return true;
            }
        });
        left_pitching_reduce.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tempTxt = left_pitching_txt;
                tempMaxValue = 1500;
                tempMinValue = 0;
                planeData = BlueBooth.EnumPlaneData.left_pitching;
                TouchDeal(event, false);
                return true;
            }
        });

        right_pitching_add.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tempTxt = right_pitching_txt;
                tempMaxValue = 3000;
                tempMinValue = 1500;
                planeData = BlueBooth.EnumPlaneData.right_pitching;
                TouchDeal(event, true);
                return true;
            }
        });
        right_pitching_reduce.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tempTxt = right_pitching_txt;
                tempMaxValue = 3000;
                tempMinValue = 1500;
                planeData = BlueBooth.EnumPlaneData.right_pitching;
                TouchDeal(event, false);
                return true;
            }
        });
    }


    //弹出setting框
    protected void showSettingDialog() {
        if (settingDialog == null) {
            settingDialog = new AlertDialog.Builder(this).create();
            settingDialog.setCancelable(false);
            //dialog点击的监听事件
            settingDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_BACK)
                        return true;
                    return false;
                }
            });
            settingDialog.show();
            settingDialog.setContentView(R.layout.activity_setting);
            settingDialog.setCanceledOnTouchOutside(true);
            settingDialog.getWindow().setBackgroundDrawableResource(R.color.colorBlue);
            //获取主界面的宽高
            ConstraintLayout constraintLayout = findViewById(R.id.activity_main);
            //设置对话框的宽高和主界面的宽高一样(满屏)
            settingDialog.getWindow().setLayout(constraintLayout.getWidth(), constraintLayout.getHeight());

            initSetting();
        } else {
            settingDialog.show();
        }

    }

    //隐藏loading框
    protected void dismissSettingDialog() {
        if (null != settingDialog && settingDialog.isShowing()) {
            settingDialog.dismiss();
        }
    }
    //--------------------------------------------------设置界面--------------------------------------//


}