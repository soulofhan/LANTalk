package com.andeddo.lanchat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ChatMsgActivity extends Activity {
    private static final String TAG = "ChatMsgActivity";
    SocketManager socketManager =new SocketManager();

    private ChatAdapter chatAdapter;
    private ListView lv_chatMsg;
    private EditText et_getMsg;
    private List<PersonChat> personChats = new ArrayList<PersonChat>();

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat_msg);

        initView();
    }

    private void initView() {
        lv_chatMsg = findViewById(R.id.lv_chatMsg);
        et_getMsg = findViewById(R.id.et_getMsg);
        Button btn_sendMsg = findViewById(R.id.btn_sendMsg);

        chatAdapter = new ChatAdapter(this,personChats);
        lv_chatMsg.setAdapter(chatAdapter);

        btn_sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(et_getMsg.getText().toString())){
                    Toast.makeText(ChatMsgActivity.this,"发送内容不能为空",Toast.LENGTH_LONG).show();
                    return;
                }
                socketManager.sendMessage(et_getMsg.getText().toString());
                PersonChat personChat = new PersonChat();
                personChat.setMeSend(true);
                personChat.setChatMsg(et_getMsg.getText().toString());
                personChats.add(personChat);
                et_getMsg.setText("");
                chatAdapter.notifyDataSetChanged();
                handler.sendEmptyMessage(1);
            }
        });
    }

    /**
     * 显示接收到的消息
     * @param getMsg 传入收到的聊天内容
     */
    public void setMsg(String getMsg){
        Log.d(TAG, "setMsg: 85");
        PersonChat personChat = new PersonChat();
        personChat.setMeSend(false);
        personChat.setChatMsg(getMsg);
        personChats.add(personChat);
        handler.sendEmptyMessage(1);
    }


}
