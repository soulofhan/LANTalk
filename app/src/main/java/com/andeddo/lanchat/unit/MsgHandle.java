package com.andeddo.lanchat.unit;

import android.util.Log;

import com.andeddo.lanchat.ChatMsgActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * onCreate by soulofhan 2018/10/10
 * 将socket接收到的数据进行处理分类后显示
 */
public class MsgHandle {
    private static final String TAG = "MsgHandle";

    private static String online = "";
    private static String tip = "";

    /**
     * 正则表达式消息处理
     *
     * @param info 传入接收到的需要处理的消息
     */
    public static void msgHandle(String info) {
        Log.d(TAG, "msgHandle 需要处理的数据为：" + info);
        MsgHandle msgHandle = new MsgHandle();
        String top = "";
        String p = "\\[(.*)\\]:\\[(.*)\\]";
        Pattern pattern = Pattern.compile(p);
        Matcher matcher = pattern.matcher(info);
        if (matcher.find()) {
            top = matcher.group(1);
        }

        if ("Msg".equals(top)) {
            msgHandle.msgInfo(info);

        } else if ("Tip".equals(top)) {
            msgHandle.tipInfo(info);

        } else if ("dis".equals(top)) {
            msgHandle.dis(info);

        } else if ("decide".equals(top)) {
            msgHandle.decide(info);
        } else {
            Log.d(TAG, "msgHandle: 46 " + info);
        }
    }

    /**
     * 更新收到的消息
     *
     * @param msgInfo 传入接收到的聊天内容消息
     */
    private void msgInfo(String msgInfo) {
        Log.d(TAG, "msgInfo: ");
        String p = "\\[Msg\\]:\\[(.*),(.*)\\]";
        Pattern pattern = Pattern.compile(p);
        Matcher matcher = pattern.matcher(msgInfo);
        if (matcher.find()) {
            Log.d(TAG, "msgInfo: " + matcher.group(1) + "2: " + matcher.group(2));
            ChatMsgActivity.setMsg(matcher.group(1), matcher.group(2));
        }
    }

    private void tipInfo(String tip) {
        Log.d(TAG, "tipInfo: " + tip);
        String pTip = "\\[Tip]:\\[(.*)\\]";
        Pattern pattern = Pattern.compile(pTip);
        final Matcher matcher = pattern.matcher(tip);
        if (matcher.find()) {
            final String runTip = matcher.group(1);
            setTip(runTip);
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        Thread.sleep(1000); // 休眠1秒
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ChatMsgActivity.setTip(runTip);
                }
            }).start();
        }
    }

    private void setTip(String tip){
        MsgHandle.tip = tip;
    }

    public static String getTip(){
        return tip;
    }

    private void dis(String unLink) {
        Log.d(TAG, "dis: ");
        String dis = "\\[dis\\]:\\[(.*)\\]";
        Pattern pattern = Pattern.compile(dis);
        Matcher matcher = pattern.matcher(unLink);
        if (matcher.find()) {
            ChatMsgActivity.setDis(matcher.group(1));
        }
    }

    private void decide(String decide) {
        Log.d(TAG, "decide: ");
        String dec = "\\[decide\\]:\\[(.*)\\]";
        Pattern pattern = Pattern.compile(dec);
        Matcher matcher = pattern.matcher(decide);
        if (matcher.find()) {
            setOnline(matcher.group(1));
        }
    }

    private void setOnline(String online) {
        MsgHandle.online = online;
    }

    public static String getOnline() {
        return online;
    }
}
