package com.android.me.tinytcpserver;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    final String TAG = "MyAPP";
    private Button button1;
    private Button button2;
    private Button button3;
    private TextView text1;
    private final TinyTcpServer tts = new TinyTcpServer();
    private Handler onConnect = null;
    private Handler onDisconnect = null;
    private Handler onRecv = null;
    String mText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button1 = (Button)findViewById(R.id.button1);
        button2 = (Button)findViewById(R.id.button2);
        button3 = (Button)findViewById(R.id.button3);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tts.start(7000);
                button1.setEnabled(false);
                button2.setEnabled(true);
                button3.setEnabled(true);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = "Hello";
                tts.send(str);
                updateChatText(str);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tts.stop();
                button1.setEnabled(true);
                button2.setEnabled(false);
                button3.setEnabled(false);
            }
        });
        text1 = (TextView)findViewById(R.id.textView1);

        onConnect = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        updateChatText("connect");
                        break;
                }
            }
        };
        onDisconnect = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        updateChatText("disconnect");
                        break;
                }
            }
        };
        onRecv = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        updateChatText(tts.getData());
                        break;
                }
            }
        };

        tts.setOnConnect(onConnect);
        tts.setOnDisconnect(onDisconnect);
        tts.setOnRecv(onRecv);

        button1.setEnabled(true);
        button2.setEnabled(false);
        button3.setEnabled(false);
    }

    @Override
    protected void onDestroy() {
        tts.stop();
        super.onDestroy();
    }

    void updateChatText(String str)
    {
        mText += str + "\n";
        text1.setText(mText);
    }
}
