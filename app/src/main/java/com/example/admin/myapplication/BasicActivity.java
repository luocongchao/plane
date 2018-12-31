package com.example.admin.myapplication;


import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.TextView;

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

    private AlertDialog alertDialog;
    protected BlueBoothManager blueBoothManager = new BlueBoothManager();
    protected onMessageCallback messageCallback;

    protected Handler handler;

    //设置消息监听事件
    protected void setOnMessageCallback(onMessageCallback onMessageCallback) {
        this.messageCallback = onMessageCallback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler= new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case BlueBooth.CONNECT_BLUEBOOTH:
                        if (messageCallback != null) messageCallback.run((String) msg.obj);
                        break;
                    default:
                        break;
                }
            }
        };
        blueBoothManager.setUpdateInvalidate(new BlueBoothManager.OnUpdateListener() {
            @Override
            public void onUpdateInvalidate(int i, String msg) {
                Message message = new Message();
                message.what = i;
                message.obj = msg;
                handler.sendMessage(message);
            }
        });

    }

    //弹出loading框
    protected void showLoadingDialog(String msg){
        showLoadingDialog();
        TextView textView=alertDialog.findViewById(R.id.dialog_txt);
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
        TextView textView=alertDialog.findViewById(R.id.dialog_txt);
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


}