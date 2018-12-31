package com.example.admin.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StartUp extends BasicActivity {
    private TextView tipe;
    private TextView addresstxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_up);


        Button connect = findViewById(R.id.startup_connect);
        Button jump = findViewById(R.id.startup_admin);

        tipe = findViewById(R.id.startup_tipe);
        addresstxt = findViewById(R.id.startup_address);
        //设置消息接收函数
        setOnMessageCallback(new onMessageCallback() {
            @Override
            public void run(String msg) {
                tipe.setText(msg);
            }
        });
        //跳转按钮的点击事件
        jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartUp.this, MainActivity.class);
                startActivity(intent);
            }
        });
        //连接按钮的点击事件
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = addresstxt.getText().toString();
                if (address != null) BlueBooth.address = address;
                showLoadingDialog("正在连接蓝牙....");
                //蓝牙连接的异步回调事件
                blueBoothManager.connectBlueTooth(new BlueBoothManager.OnCallbackListener() {
                    @Override
                    public void onCallback(boolean state) {
                        if (state) {
                            Intent intent = new Intent(StartUp.this, MainActivity.class);
                            startActivity(intent);
                        }
                        dismissLoadingDialog();
                    }
                });
            }
        });
        //判断蓝牙的连接状态
        if (BlueBooth.state) tipe.setText("当前状态:已连接");
        else tipe.setText("当前状态:未连接");
    }
}
