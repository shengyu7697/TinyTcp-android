package com.android.me.tinytcpserver;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    final String TAG = "MyAPP";
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private EditText editText1;
    private TextView text1;
    private final TinyTcpServer ts = new TinyTcpServer();
    //private final TinyUdpServer ts = new TinyUdpServer();
    private Handler onConnect = null;
    private Handler onDisconnect = null;
    private Handler onRecv = null;
    String mText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button1 = (Button)findViewById(R.id.button1); // bind
        button2 = (Button)findViewById(R.id.button2); // send
        button3 = (Button)findViewById(R.id.button3); // stop
        button4 = (Button)findViewById(R.id.button4); // clear
        editText1 = (EditText)findViewById(R.id.editText1); // port
        text1 = (TextView)findViewById(R.id.textView1);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int port = Integer.parseInt(editText1.getText().toString());
                ts.start(port);
                updateChatText("bind on port: " + port);
                button1.setEnabled(false);
                button2.setEnabled(true);
                button3.setEnabled(true);
                button4.setEnabled(true);
                editText1.setEnabled(false);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = "Hello";
                ts.send(str);
                updateChatText(str);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ts.stop();
                button1.setEnabled(true);
                button2.setEnabled(false);
                button3.setEnabled(false);
                button4.setEnabled(false);
                editText1.setEnabled(true);
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearChatText();
            }
        });

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
                    case 0: // tcp
                        updateChatText(ts.getData());
                        break;
                    case 1: // udp
                        updateChatText(ts.getData());
                        break;
                }
            }
        };

        ts.setOnConnect(onConnect);
        ts.setOnDisconnect(onDisconnect);
        ts.setOnRecv(onRecv);

        button1.setEnabled(true);
        button2.setEnabled(false);
        button3.setEnabled(false);
        button4.setEnabled(false);
        editText1.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        ts.stop();
        super.onDestroy();
    }

    void updateChatText(String str)
    {
        mText += str + "\n";
        text1.setText(mText);
    }

    void clearChatText()
    {
        mText = "";
        text1.setText(mText);
    }
}
