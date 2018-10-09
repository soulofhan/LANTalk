package com.andeddo.lanchat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SocketManager extends Thread{
    private static final String TAG = "SocketManager";
    private static final String disConnect = "disconnect";


    private Socket socket;
    private String HOST;
    private int PORT;
    private BufferedReader bufferedReader;
    private static BufferedWriter bufferedWriter;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            ChatMsgActivity chatMsgActivity = new ChatMsgActivity();
            switch (what){
                case 1:
                    chatMsgActivity.setMsg(msg.getData().getString("name"),msg.getData().getString("msg"));
                    break;
                case 2:
                    chatMsgActivity.setTip(msg.getData().getString("msg"));

            }
        }
    };

    /**
     *
     * @param IPAddress 传入连接的IP地址
     * @param iPORT 传入连接的端口号
     */
    public SocketManager(String IPAddress, int iPORT) {
        HOST = IPAddress;
        PORT = iPORT;
    }

    @Override
    public void run() {
        String disconnect = "connect";
        try {
            socket = new Socket(HOST,PORT);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Log.d(TAG, "run: 接收到的数据为" + line);
                if(line.equals(disConnect)){
                    disconnect = disConnect;
                    break;
                }else{
                    // 将接收到的信息进行分类并显示
                    msgHandle(line);
                }
            }
            Log.d(TAG, "run: 退出消息接收阻塞...");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "run: 64" + e);
        } finally {
            if(disConnect.equals(disconnect)) {
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
     * @param msg 聊天内容
     */
    public static void sendMessage(String msg){
        Log.d(TAG, "sendMessage: 80 " + msg);
        if(bufferedWriter != null){
            try {
                bufferedWriter.write(msg);
                bufferedWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 正则表达式消息处理
     * @param info 传入接收到的需要处理的消息
     */
    private void msgHandle(String info) {
        String top = "";
        String name = "";
        String msg = "";
        String p = "\\[(.*)\\]:\\[(.*),(.*)\\]";
        Pattern pattern = Pattern.compile(p);
        Matcher matcher = pattern.matcher(info);
        if(matcher.find()){
            top = matcher.group(1);
            name = matcher.group(2);
            msg = matcher.group(3);
        }

        if("Msg".equals(top)) {
            Log.d(TAG, "msgHandle: top");
            Message message = new Message();
            message.what = 1;
            Bundle bundle = new Bundle();
            bundle.putString("name",name);
            bundle.putString("msg",msg);
            message.setData(bundle);
            mHandler.sendMessage(message);
        }else if("disconnect".equals(top)) {
            //执行显示用户退出房间提示
        }else {
            Log.d(TAG, "msgHandle: "+info);
            p = "\\['(.*)'\\]";
            pattern = Pattern.compile(p);
            matcher = pattern.matcher(info);
            if(matcher.find()) {
                name = matcher.group(1);
                Log.d(TAG, "msgHandle: 140" + name);
            }else{
                return;
            }
            Message message = new Message();
            message.what = 2;
            Bundle bundle = new Bundle();
            bundle.putString("msg",name);
            message.setData(bundle);
            mHandler.sendMessage(message);
        }
    }

}
