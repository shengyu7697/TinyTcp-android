package com.android.me.tinytcpclient;

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
    private EditText editText2;
    private TextView text1;
    private final TinyTcpClient tc = new TinyTcpClient();
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
        editText1 = (EditText)findViewById(R.id.editText1); // hostname
        editText2 = (EditText)findViewById(R.id.editText2); // port
        text1 = (TextView)findViewById(R.id.textView1);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String hostname = editText1.getText().toString();
                int port = Integer.parseInt(editText2.getText().toString());
                tc.start(hostname, port);
                updateChatText("connect to " + hostname + ":" + port);
                button1.setEnabled(false);
                button2.setEnabled(true);
                button3.setEnabled(true);
                button4.setEnabled(true);
                editText1.setEnabled(false);
                editText2.setEnabled(false);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = "Hello Server";
                tc.send(str);
                updateChatText(str);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tc.stop();
                button1.setEnabled(true);
                button2.setEnabled(false);
                button3.setEnabled(false);
                button4.setEnabled(false);
                editText1.setEnabled(true);
                editText2.setEnabled(true);
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
                    case 0:
                        updateChatText(tc.getData());
                        break;
                }
            }
        };

        tc.setOnConnect(onConnect);
        tc.setOnDisconnect(onDisconnect);
        tc.setOnRecv(onRecv);

        button1.setEnabled(true);
        button2.setEnabled(false);
        button3.setEnabled(false);
        button4.setEnabled(false);
        editText1.setEnabled(true);
        editText2.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        tc.stop();
        super.onDestroy();
    }

    void updateChatText(String str) {
        mText += str + "\n";
        text1.setText(mText);
    }

    void clearChatText() {
        mText = "";
        text1.setText(mText);
    }
}
