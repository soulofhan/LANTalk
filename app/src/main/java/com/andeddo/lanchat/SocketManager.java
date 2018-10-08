package com.andeddo.lanchat;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class SocketManager extends Thread{
    private static final String TAG = "SocketManager";
    private static final String disConnect = "disconnect";


    private Socket socket;
    private String HOST;
    private int PORT;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    ChatMsgActivity chatMsgActivity = new ChatMsgActivity();


    /**
     *
     * @param IPAddress 传入连接的IP地址
     * @param iPORT 传入连接的端口号
     */
    public SocketManager(String IPAddress, int iPORT) {
        HOST = IPAddress;
        PORT = iPORT;
    }

    public SocketManager() {
        super();
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
                    /** 将接收到的信息进行分类并显示 */
                    chatMsgActivity.setMsg(line);
                }
            }
            Log.d(TAG, "run: 退出消息接收阻塞...");
        } catch (IOException e) {
            e.printStackTrace();
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
    public void sendMessage(String msg){
        Log.d(TAG, "sendMessage: 80 " + msg);
        if(bufferedWriter != null){
            try {
                bufferedWriter.write(msg + "\n");
                bufferedWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
