package com.andeddo.lanchat;

import android.util.Log;

import com.andeddo.lanchat.unit.MsgHandle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

public class SocketManager extends Thread {
    private static final String TAG = "SocketManager";
    private static final String disConnect = "disconnect";

//    private static String IPAddress = "192.168.31.158";
    private static String IPAddress = "192.168.10.129";
    private static int PORT = 5963;

    private static Socket socket;
    private static BufferedReader bufferedReader;
    private static BufferedWriter bufferedWriter;
    private String disconnect = "connect";
    private static boolean cut;
    private static boolean lose;

    @Override
    public void run() {
        try {
            Log.d(TAG, "run: 开始链接socket服务器");
//            socket = new Socket(IPAddress, PORT);
            socket = new Socket();
            SocketAddress endpoint = new InetSocketAddress(IPAddress,PORT);
            socket.connect(endpoint,10000);
            if (!cut) {
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

                sendMessage("1");
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.equals(disConnect)) {
                        disconnect = disConnect;
                        cut = true;
                        break;
                    } else {
                        // 将接收到的信息进行分类并显示
                        MsgHandle msgHandle = new MsgHandle(line);
                        msgHandle.msgSort();
                    }
                }
            }
            Log.d(TAG, "55 run: 退出消息接收阻塞...");
        }catch (SocketTimeoutException time){
            IPAddress = "192.168.10.129";
            PORT = 5963;
            run();
        } catch (IOException e) {
            lose = true;
            socketClose();
        } finally {
            if (disConnect.equals(disconnect)) {
                socketClose();
            }
        }
    }

    public static void socketClose() {
        try {
            bufferedWriter.close();
            bufferedReader.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送聊天内容
     *
     * @param msg 聊天内容
     */
    public static void sendMessage(String msg) {
        if (bufferedWriter != null) {
            try {
                bufferedWriter.write(msg);
                bufferedWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setStatus(){
        cut = false;
        lose = false;
    }

    public static boolean getCut() {
        return cut;
    }

    public static boolean getLose() {
        return lose;
    }
}
