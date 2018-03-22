package com.android.me.tinytcpserver;

import android.os.Handler;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.ServerSocket;
import java.nio.ByteBuffer;

public class TinyTcpServer {
    final String TAG = "TinyTcpServer";
    private Thread mThread;
    private ServerSocket mServerSocket = null;
    private Socket mSocket = null;
    private String mData;
    private Handler onConnect = null;
    private Handler onDisconnect = null;
    private Handler onRecv = null;
    private int mPort;

    public TinyTcpServer() {
    }

    void start(int port)
    {
        Log.d(TAG, "start");

        mPort = port;

        mThread = new Thread(runnable);
        mThread.start();

        if (mThread.isAlive())
            Log.d(TAG, "thread alive");
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "run");

            ByteBuffer byteBuffer = ByteBuffer.allocate(96);
            int bytesRead;

            try {
                mServerSocket = new ServerSocket(mPort);

                while (mServerSocket != null) {
                    mSocket = mServerSocket.accept();
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
                }

            } catch(Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Exception = " + e.toString());
            }
        }
    };

    void stop()
    {
        Log.d(TAG, "stop");
        try {
            mSocket.close();
            mServerSocket.close();
            mServerSocket = null;
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
    synchronized void setData(String data) { mData = data; }
}