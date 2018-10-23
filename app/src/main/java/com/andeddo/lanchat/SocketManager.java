package com.andeddo.lanchat;

import android.util.Log;

import com.andeddo.lanchat.unit.MsgHandle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class SocketManager extends Thread {
    private static final String TAG = "SocketManager";
    private static final String disConnect = "disconnect";

    private static String IPAddress = "192.168.31.158";
//    private final static String IPAddress = "192.168.10.129";
    private final static int PORT = 5962;

    private Socket socket;
    private BufferedReader bufferedReader;
    private static BufferedWriter bufferedWriter;
    private String disconnect = "connect";
    private static boolean cut = false;

    @Override
    public void run() {
        try {
            Log.d(TAG, "run: 开始链接socket服务器");
            socket = new Socket(IPAddress,PORT);
            socket = new Socket();
            SocketAddress endpoint = new InetSocketAddress(IPAddress,PORT);
            socket.connect(endpoint,10000);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

            sendMessage("1");
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Log.d(TAG, "run: 接收到的数据为：" + line);
                if (line.equals(disConnect)) {
                    disconnect = disConnect;
                    cut = true;
                    break;
                } else {
                    // 将接收到的信息进行分类并显示
                    MsgHandle.msgHandle(line);
                }
            }
            Log.d(TAG, "run: 退出消息接收阻塞...");
        } catch (ConnectException c) {
            Log.d(TAG, "60 连接服务器超时" + c);
            IPAddress = "192.168.10.129";
            run();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (disConnect.equals(disconnect)) {
                try {
                    bufferedWriter.close();
                    bufferedReader.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 发送聊天内容
     *
     * @param msg 聊天内容
     */
    public static void sendMessage(String msg) {
        Log.d(TAG, "sendMessage: 70 " + msg);
        if (bufferedWriter != null) {
            try {
                bufferedWriter.write(msg);
                bufferedWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean getCut(){
        return cut;
    }

}
