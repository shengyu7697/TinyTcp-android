package com.android.me.tinytcpclient;

import android.os.Handler;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by shengyu on 4/17/18.
 */

public class TinyUdpClient {
    final String TAG = "TinyTcpClient";
    private Thread mThread;
    DatagramSocket mSocket;
    private Handler onConnect = null;
    private Handler onDisconnect = null;
    private Handler onRecv = null;
    //private String mIpAddress;
    private InetAddress mServerAddr;
    private int mPort;

    TinyUdpClient() {

    }

    public void start(String ipAddress, int port) {
        Log.d(TAG, "start");

        //mIpAddress = ipAddress;
        try {
            mServerAddr = InetAddress.getByName(ipAddress);
        } catch(Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception = " + e.toString());
        }
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

            final int SIZE = 8192;
            byte buf[] = new byte[SIZE];
            try {
                mSocket = new DatagramSocket();

                DatagramPacket packet = new DatagramPacket(buf, buf.length, mServerAddr, mPort);
                mSocket.send(packet);
            } catch(Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Exception = " + e.toString());
            }
        }
    };

    public void stop() {
        Log.d(TAG, "stop");
        try {

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void send(final String data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void setOnConnect(Handler h) { onConnect = h; }
    public void setOnDisconnect(Handler h) { onDisconnect = h; }
    public void setOnRecv(Handler h) { onRecv = h; }
}
