package com.andeddo.lanchat.unit;

import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

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
    private static String success = "pass";
    private static boolean WELCOME = false;

    /**
     * 正则表达式消息处理
     *
     * @param info 传入接收到的需要处理的消息
     */
    public static void msgHandle(String info) {
        Log.d(TAG, "msgHandle 需要处理的数据为：" + info);
        MsgHandle msgHandle = new MsgHandle();
        String top = "";
        String p = "\\[(.*)\\]:";
        Pattern pattern = Pattern.compile(p);
        Matcher matcher = pattern.matcher(info);
        if (matcher.find()) {
            top = matcher.group(1);
            Log.d(TAG, "msgHandle: 截取的报头信息：" + top);
        }

        if ("Msg".equals(top)) {
            msgHandle.msgInfo(info);

        } else if ("Tip".equals(top)) {
            msgHandle.tipInfo(info);

        } else if ("Dis".equals(top)) {
            msgHandle.dis(info);

        } else if ("decide".equals(top)) {
            msgHandle.decide(info);

        } else if ("enter".equals(top)) {
            msgHandle.enter(info);

        } else if ("correct".equals(top)) {
            msgHandle.success(info);

        } else if ("wel".equals(top)) {
            WELCOME = true;

        } else if ("proclamation".equals(top)) {
            msgHandle.proclamationToast(info);
        } else {
            Log.d(TAG, "msgHandle: 52 " + info);
        }
    }

    /**
     * 更新收到的消息
     *
     * @param msgInfo 传入接收到的聊天内容消息
     */
    private void msgInfo(String msgInfo) {
        Log.d(TAG, "msgInfo: " + msgInfo);
        String p = "\\[Msg\\]:\\[(.*),(.*)\\]";
        Pattern pattern = Pattern.compile(p);
        Matcher matcher = pattern.matcher(msgInfo);
        if (matcher.find()) {
            ChatMsgActivity.setMsg(matcher.group(1), matcher.group(2));
        }
    }

    //显示当前在线人员
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
                        Thread.sleep(1000); // 休眠0.5秒
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ChatMsgActivity.setTip(runTip);
                }
            }).start();
        }
    }

    //断开连接
    private void dis(String unLink) {
        Log.d(TAG, "dis: " + unLink);
        String dis = "\\[Dis\\]:(.*)";
        Pattern pattern = Pattern.compile(dis);
        Matcher matcher = pattern.matcher(unLink);
        if (matcher.find()) {
            ChatMsgActivity.setDis(1, matcher.group(1));
        }
    }

    //首次进入初始化名字
    private void decide(String decide) {
        Log.d(TAG, "decide: " + decide);
        String dec = "\\[decide\\]:\\[(.*)\\]";
        Pattern pattern = Pattern.compile(dec);
        Matcher matcher = pattern.matcher(decide);
        if (matcher.find()) {
            setOnline(matcher.group(1));
        }
    }

    //提示用户进入房间
    private void enter(String enter) {
        Log.d(TAG, "enter: " + enter);
        String ent = "\\[enter\\]:(.*)";
        Pattern pattern = Pattern.compile(ent);
        Matcher matcher = pattern.matcher(enter);
        if (matcher.find()) {
            ChatMsgActivity.setDis(2, matcher.group(1));
        }
    }

    //连接成功
    private void success(String correct) {
        String suc = "\\[correct\\]:\\[(.*)\\]";
        Pattern pattern = Pattern.compile(suc);
        Matcher matcher = pattern.matcher(correct);
        if (matcher.find()) {
            setSuccess(matcher.group(1));
        }
    }

    //收到系统公告并吐司显示
    private void proclamationToast(String proclamation) {
        String suc = "\\[proclamation\\]:\\[(.*)\\]";
        Pattern pattern = Pattern.compile(suc);
        final Matcher matcher = pattern.matcher(proclamation);
        if (matcher.find()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    Toast.makeText(myApplication.getContext(), matcher.group(1), Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }).start();
        }
    }

    private void setOnline(String online) {
        MsgHandle.online = online;
    }

    //获取在线人数
    public static String getOnline() {
        return online;
    }

    private void setTip(String tip) {
        MsgHandle.tip = tip;
    }

    //当前在线人员名字
    public static String getTip() {
        return tip;
    }

    private void setSuccess(String ok) {
        MsgHandle.success = ok;
    }

    //进入聊天室成功
    public static String getSuccess() {
        return success;
    }

    public static void setWel() {
        MsgHandle.WELCOME = false;
    }

    //连接服务器成功
    public static boolean getWel() {
        return WELCOME;
    }
}
