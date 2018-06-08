package com.android.me.tinytcpserver;

import android.os.Handler;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Created by shengyu on 4/17/18.
 */

public class TinyUdpServer {
    final String TAG = "TinyUdpServer";
    private Thread mThread;
    DatagramSocket mSocket = null;
    private Deque<String> mData = new ArrayDeque<>();
    private Handler onRecv = null;
    private int mPort;

    public TinyUdpServer() {
    }

    public void start(int port) {
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

            byte buf[] = new byte[4096];
            try {
                mSocket = new DatagramSocket(mPort);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Exception = " + e.toString());
            }

            while (mSocket != null) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                try {
                    mSocket.receive(packet);
                } catch (Exception e) {
                    // when stop() was called, throw the exception. just break.
                    /*e.printStackTrace();
                    Log.e(TAG, "Exception = " + e.toString());*/
                    break;
                }
                //Log.d(TAG, "bytesRead: " + packet.getLength());
                //Log.d(TAG, "recv from:" + packet.getAddress().toString() + ":" + packet.getPort());
                String str = new String(buf, 0, packet.getLength());
                setData(str);
                if (onRecv != null) {
                    onRecv.sendEmptyMessage(1);
                }

/*Log.d(TAG, str);
packet.setData(new String("123").getBytes());
byte buf2[] = new String("123").getBytes();
DatagramPacket packet2 = new DatagramPacket(buf2, buf2.length,
InetAddress.getByName("10.x.x.x"), 7001);
mSocket.send(packet2);*/
            }
            Log.d(TAG, "end");
        }
    };

    public void stop() {
        Log.d(TAG, "stop");
        try {
            if (mSocket != null) {
                mSocket.close();
                mSocket = null;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void send(final String data) {
        byte[] buf = data.getBytes();
        try {
            //DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddr, 3333);
            //mSocket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception = " + e.toString());
        }
    }

    public void setOnRecv(Handler h) { onRecv = h; }
    synchronized String getData() { return mData.poll(); }
    synchronized void setData(String data) { mData.add(data); }
}
