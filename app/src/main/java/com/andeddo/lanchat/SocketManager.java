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
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class SocketManager extends Thread {
    private static final String TAG = "SocketManager";
    private static final String disConnect = "disconnect";

    private static String IPAddress;
//    private final static int PORT = 16903;    //python
    private final static int PORT = 26096;  //java

    private static Socket socket;
    private static BufferedReader bufferedReader;
    private static BufferedWriter bufferedWriter;
    private static boolean cut;     //用于判断是否处于断开状态 -- 主动断开
    private static boolean lose;    //用于判断socket是否连接 -- 被动断开 服务器重启 掉线等
    private static ChatMsgActivityView chatView;

    @Override
    public void run() {
        try {
            Log.d(TAG, "run: 开始链接socket服务器--" + IPAddress);
            socket = new Socket();
            SocketAddress socketAddress = new InetSocketAddress(IPAddress, PORT);    //绑定ip地址与端口
            socket.connect(socketAddress, 10000);    //设置连接超时时间 使用SocketTimeoutException捕获超时
            if (!cut) {     //cut 为false时执行
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

                sendMessage("1\n");   //向服务器发送连接请求
                String line;
                while (true) {
                    line = bufferedReader.readLine();
                    if (line == null || line.equals(disConnect)) {
                        cut = true;
                        break;
                    } else {
                        // 将接收到的信息进行分类并显示
                        MsgHandle msgHandle = new MsgHandle(line);
                        msgHandle.msgSort();
                    }
                }
                Log.d(TAG, "59 run: 退出消息接收阻塞...");
            }
        } catch (SocketTimeoutException time) {     //超时异常
            Log.d(TAG, "62 超时异常: " + time);
            hostChange();   //连接超时换ip
        } catch (ConnectException c) {      //ip不存在连接异常
            Log.d(TAG, "65 连接服务器异常: " + c);
            hostChange();
        } catch (SocketException s) {       //服务器重启关闭等
            Log.d(TAG, "68 套接字异常: " + s);
            lose = true;    //服务器掉线\关闭等
            socketClose();
        } catch (IOException e) {           //缓冲区关闭异常
            Log.d(TAG, "72 读取异常: " + e);
            lose = true;    //服务器掉线\关闭等
            socketClose();
        } finally {
            if (cut) {      //cut 为true时为连接断开
                chatView.mHandle();
                socketClose();
            }
        }
    }

    private void hostChange() {
        if ("192.168.10.129".equals(IPAddress)) {
            IPAddress = "192.168.10.50";
            run();
        } else if ("192.168.10.50".equals(IPAddress)) {
            IPAddress = "192.168.31.158";
            run();
        } else if ("192.168.31.158".equals(IPAddress)) {
            IPAddress = "192.168.10.148";
            run();
        } else {
            Log.d(TAG, "hostChange: 未能找到服务器，请稍后重试");
            MsgHandle msgHandle = new MsgHandle("[correct]:[unKnow]");
            msgHandle.msgSort();
        }
    }

    public static void socketClose() {
        try {
            if (bufferedWriter != null && bufferedReader != null) {
                bufferedWriter.close();
                bufferedReader.close();
            }
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
    public static void sendMessage(final String msg) {
        Log.d(TAG, "sendMessage: " + msg + "是否为空：" + msg.isEmpty());
        new Thread() {
            @Override
            public void run() {
                if (bufferedWriter != null && !msg.isEmpty()) {
                    try {
                        bufferedWriter.write(msg + "\n");
                        bufferedWriter.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public void setStatus(String HOST, ChatMsgActivityView chatMsgActivityView) {
        cut = false;
        lose = false;
        IPAddress = HOST;
        chatView = chatMsgActivityView;
    }

    public static void setView(ChatMsgActivityView chatMsgActivityView) {
        chatView = chatMsgActivityView;
        Log.d(TAG, "setView: " + chatView);
    }

    public static boolean getCut() {
        return cut;
    }

    public static boolean getLose() {
        return lose;
    }
}
