package com.andeddo.lanchat;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketLink {
    private final static String TAG = "---SocketLink---";

    private String HOST;
    private int PORT;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    /**
     *
     * @param IPAddress 传入连接的IP地址
     * @param iPORT 传入连接的端口号
     */
    public SocketLink(String IPAddress, int iPORT) {
        HOST = IPAddress;
        PORT = iPORT;
    }

    public void connect() {

        try {
            Socket mSocket = new Socket();
            Log.d(TAG, "connect HOST: "+ HOST +" PORT:"+ PORT);
            mSocket.connect(new InetSocketAddress(HOST, PORT), 30000);                      //设置超时为30秒

            bufferedReader = new BufferedReader(new InputStreamReader(
                    mSocket.getInputStream()));

            bufferedWriter = new BufferedWriter(new OutputStreamWriter(
                    mSocket.getOutputStream(), "utf-8"));



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
