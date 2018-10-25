package com.andeddo.lanchat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.andeddo.lanchat.unit.MsgHandle;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;

import java.util.ArrayList;
import java.util.List;

public class ChatMsgActivity extends Activity {
    private static final String TAG = "ChatMsgActivity";
    private static boolean isFocus = false;

    private static TitleBar chat_titleBar;
    private static ListView lv_chatMsg;
    private EditText et_getMsg;

    private ChatAdapter chatAdapter;
    ProgressDialog waitingDialog;
    private static List<PersonChat> personChats = new ArrayList<PersonChat>();

    @SuppressLint("HandlerLeak")
    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case 1:
                    lv_chatMsg.setSelection(personChats.size());
                    handler.sendEmptyMessage(2);
                    break;
                case 2:
                    chat_titleBar.setRightTitle("在线:" + MsgHandle.getOnline());
                    break;
                default:
                    break;
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case 3:
                    if (SocketManager.getLose()) {
                        Toast.makeText(getApplicationContext(), "连接已丢失,返回主页", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        mHandler.sendEmptyMessageDelayed(3, 1000);
                    }
                    break;
                case 4:
                    if (SocketManager.getCut()) {
                        waitingDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "已断开与服务器的连接", Toast.LENGTH_SHORT).show();
                        finish();
                    }
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
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //显示状态栏
        chat_titleBar = findViewById(R.id.chat_titleBar);
        chat_titleBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View v) {

                SocketManager.sendMessage("disconnect");
                showWaitingDialog();
                mHandler.sendEmptyMessage(4);
            }

            @Override
            public void onTitleClick(View v) {
                Toast.makeText(getApplicationContext(), R.string.modify_Room, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightClick(View v) {
                Toast.makeText(getApplicationContext(), String.format(getResources().getString(R.string.dialog), MsgHandle.getOnline()), Toast.LENGTH_SHORT).show();
            }
        });

        lv_chatMsg = findViewById(R.id.lv_chatMsg);
        Button btn_sendMsg = findViewById(R.id.btn_sendMsg);
        et_getMsg = findViewById(R.id.et_getMsg);

        btn_sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                mClickListener();
            }
        });
    }

    private void mClickListener() {
        if (TextUtils.isEmpty(et_getMsg.getText().toString())) {
            Toast.makeText(ChatMsgActivity.this, getResources().getString(R.string.not_empty), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onResume() {
        super.onResume();
        chatAdapter = new ChatAdapter(this, personChats);
        lv_chatMsg.setAdapter(chatAdapter);
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

    /**
     * 显示当前在线人昵称
     *
     * @param setMsg 在线人昵称
     */
    public static void setTip(String setMsg) {
        Log.d(TAG, "setTip: 94" + isFocus);
        String online;
        if (isFocus) {
            online = "当前在线：" + setMsg;
        } else {
            online = "当前在线: " + MsgHandle.getTip();
        }
        PersonChat personChat = new PersonChat();
        personChat.setTip(true);
        personChat.setChatMsg(online);
        personChats.add(personChat);
        handler.sendEmptyMessage(1);
    }

    /**
     * 有人退出聊天室提示
     *
     * @param setDis 退出人昵称
     */
    public static void setDis(int what, String setDis) {
        Log.d(TAG, "setDis: 100");
        String dis = "";
        switch (what) {
            case 1:
                dis = setDis + " 退出了房间";
                break;
            case 2:
                dis = setDis + " 进入了房间";
                break;
        }
        PersonChat personChat = new PersonChat();
        personChat.setTip(true);
        personChat.setChatMsg(dis);
        personChats.add(personChat);
        handler.sendEmptyMessage(1);
    }

    public void showWaitingDialog() {
        /* 等待Dialog具有屏蔽其他控件的交互能力
         * @setCancelable 为使屏幕不可点击，设置为不可取消(false)
         * 下载等事件完成后，主动调用函数关闭该Dialog
         */
        waitingDialog = new ProgressDialog(ChatMsgActivity.this);
        waitingDialog.setTitle("退出服务器");
        waitingDialog.setMessage("正在断开与服务器的连接......");
        waitingDialog.setIndeterminate(true);
        waitingDialog.setCancelable(false);
        waitingDialog.create();
        waitingDialog.show();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            isFocus = true;
            mHandler.sendEmptyMessage(3);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("提示：");
        builder.setMessage("是否退出?");
        //设置确定按钮
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        //设置取消按钮
        builder.setPositiveButton("取消",null);
        //显示弹窗
        builder.show();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: 退出ChatMsgActivity界面");
        chatAdapter.notifyDataSetInvalidated();
        super.onDestroy();
    }
}
