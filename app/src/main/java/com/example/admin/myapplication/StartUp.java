package com.example.admin.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StartUp extends AppCompatActivity {
    private BlueBoothManager blueBoothManager = new BlueBoothManager();
    private TextView tipe;
    private TextView addresstxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_up);
        blueBoothManager.setUpdateInvalidate(new BlueBoothManager.OnUpdateListener() {
            @Override
            public void onUpdateInvalidate(int i, String msg) {
                Message message = new Message();
                message.what = i;
                message.obj = msg;
                handler.sendMessage(message);
            }
        });

        Button connect = findViewById(R.id.startup_connect);
        addresstxt = findViewById(R.id.startup_address);
        Button jump = findViewById(R.id.startup_admin);
        tipe=findViewById(R.id.startup_tipe);
        jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartUp.this, MainActivity.class);
                startActivity(intent);
            }
        });
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = addresstxt.getText().toString();
                if (address != null) BlueBooth.address = address;
                blueBoothManager.connectBlueTooth(new BlueBoothManager.OnCallbackListener() {
                    @Override
                    public void onCallback() {
                        if (BlueBooth.state) {
                            Intent intent = new Intent(StartUp.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        });
        if (BlueBooth.state) tipe.setText("当前状态:已连接");
        else tipe.setText("当前状态:未连接");
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
}
