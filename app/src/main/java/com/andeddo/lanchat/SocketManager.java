package com.andeddo.lanchat;

import android.util.Log;
import android.widget.Toast;

import com.andeddo.lanchat.unit.MsgHandle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class SocketManager extends Thread {
    private static final String TAG = "SocketManager";
    private static final String disConnect = "disconnect";

//    private final static String IPAddress = "192.168.31.158";
    private final static String IPAddress = "192.168.10.129";
    private final static int PORT = 5963;

    private Socket socket;
    private BufferedReader bufferedReader;
    private static BufferedWriter bufferedWriter;
    String disconnect = "connect";
    static boolean cut = false;

    @Override
    public void run() {
        try {
            socket = new Socket(IPAddress, PORT);
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
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "run: 50" + e);
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
