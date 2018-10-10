package com.andeddo.lanchat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ChatMsgActivity extends Activity {
    private static final String TAG = "ChatMsgActivity";

    private ChatAdapter chatAdapter;
    private static ListView lv_chatMsg;
    private static List<PersonChat> personChats = new ArrayList<PersonChat>();

    @SuppressLint("HandlerLeak")
    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case 1:
                    lv_chatMsg.setSelection(personChats.size());
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_msg);

        lv_chatMsg = findViewById(R.id.lv_chatMsg);
        Button btn_sendMsg = findViewById(R.id.btn_sendMsg);
        final EditText et_getMsg = findViewById(R.id.et_getMsg);

        chatAdapter = new ChatAdapter(this, personChats);
        lv_chatMsg.setAdapter(chatAdapter);

        btn_sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_getMsg.getText().toString())) {
                    Toast.makeText(ChatMsgActivity.this, "发送内容不能为空", Toast.LENGTH_LONG).show();
                    return;
                }
                PersonChat personChat = new PersonChat();
                personChat.setMeSend(true);
                personChat.setChatMsg(et_getMsg.getText().toString());
                personChats.add(personChat);
                chatAdapter.notifyDataSetChanged();
                handler.sendEmptyMessage(1);
                SocketManager.sendMessage(et_getMsg.getText().toString());
                et_getMsg.setText("");

            }
        });
    }

    /**
     * 显示接收到的消息
     *
     * @param name   传入发信人昵称
     * @param getMsg 传入收到的聊天内容
     */
    public static void setMsg(String name, String getMsg) {
        Log.d(TAG, "setMsg: 84");
        PersonChat personChat = new PersonChat();
        personChat.setMeSend(false);
        personChat.setName(name);
        personChat.setChatMsg(getMsg);
        personChats.add(personChat);
        handler.sendEmptyMessage(1);
    }

    public static void setTip(String setMsg) {
        Log.d(TAG, "setTip: 94");
        String online = "当前在线：" + setMsg;
        PersonChat personChat = new PersonChat();
        personChat.setTip(true);
        personChat.setChatMsg(online);
        personChats.add(personChat);
        handler.sendEmptyMessage(1);
    }

    public static void setDis(String setDis) {
        Log.d(TAG, "setDis: 100");
        String dis = setDis + "退出了房间";
        PersonChat personChat = new PersonChat();
        personChat.setTip(true);
        personChat.setChatMsg(dis);
        personChats.add(personChat);
        handler.sendEmptyMessage(1);
    }

}
