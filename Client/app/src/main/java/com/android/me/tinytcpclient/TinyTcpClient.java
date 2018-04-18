package com.android.me.tinytcpclient;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class TinyTcpClient {
    final String TAG = "TinyTcpClient";
    private Thread mThread;
    private Socket mSocket = null;
    private String mData;
    private Handler onConnect = null;
    private Handler onDisconnect = null;
    private Handler onRecv = null;
    private String mIpAddress;
    private int mPort;

    public TinyTcpClient() {
    }

    public void start(String ipAddress, int port)
    {
        Log.d(TAG, "start");

        mIpAddress = ipAddress;
        mPort = port;

        mThread = new Thread(runnable);
        mThread.start();

        if (mThread.isAlive())
            Log.d(TAG, "thread alive");
    }

    private Runnable runnable = new Runnable(){
        @Override
        public void run() {
            Log.d(TAG, "run");

            ByteBuffer byteBuffer = ByteBuffer.allocate(96);
            int bytesRead;

            try {
                mSocket = new Socket(mIpAddress, mPort);
                if (onConnect != null) {
                    onConnect.sendEmptyMessage(0);
                }

                InputStream inputStream = mSocket.getInputStream();
                while (true) {
                    String text;
                    if ((bytesRead = inputStream.read(byteBuffer.array())) == -1)
                        break;

                    text = new String(byteBuffer.array(), 0, bytesRead);
                    setData(text);
                    if (onRecv != null) {
                        onRecv.sendEmptyMessage(0);
                    }
                }

                if (onDisconnect != null) {
                    onDisconnect.sendEmptyMessage(0);
                }
                mSocket.close();

            } catch(Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Exception = " + e.toString());
            }
        }
    };

    public void stop()
    {
        Log.d(TAG, "stop");
        try {
            mSocket.close();
            mSocket = null;
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void send(final String data)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OutputStream outputStream = mSocket.getOutputStream();
                    outputStream.write(data.getBytes());
                    outputStream.flush();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void setOnConnect(Handler h) { onConnect = h; }
    public void setOnDisconnect(Handler h) { onDisconnect = h; }
    public void setOnRecv(Handler h) { onRecv = h; }
    synchronized String getData() { return mData; }
    synchronized void setData(String data)
    {
        mData = data;
    }
}