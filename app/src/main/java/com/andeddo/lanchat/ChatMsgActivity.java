package com.andeddo.lanchat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

public class ChatMsgActivity extends Activity implements ChatMsgActivityView{
    private static final String TAG = "ChatMsgActivity";
    private static boolean isFocus = false;

    private static TitleBar chat_titleBar;
    private static ListView lv_chatMsg;
    private EditText et_getMsg;

    private String myName;      //用于显示自己昵称

    private ChatAdapter chatAdapter;
    ProgressDialog waitingDialog;
    private static List<PersonChat> personChats = new ArrayList<PersonChat>();

    static SoundPoolManager soundPoolManager;       //定义音效播放器

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
                        Toast.makeText(getApplicationContext(), getMsg(R.string.returnLost), Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    } else {
                        mHandler.sendEmptyMessageDelayed(3, 1000);
                    }
                    break;
                case 4:
                    showWaitingDialog();
                    if (SocketManager.getCut()) {
                        waitingDialog.dismiss();
                        Toast.makeText(getApplicationContext(), getMsg(R.string.serverLost), Toast.LENGTH_SHORT).show();
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
        SocketManager.setView(this);        //将view传入子线程中，方便子线程调用本class函数
        Intent intent = getIntent();
        myName = intent.getStringExtra("decideName");   //获取自己的昵称,用于显示

        soundPoolManager = SoundPoolManager.getInstance(ChatMsgActivity.this);      //初始化音效播放器
        chat_titleBar = findViewById(R.id.chat_titleBar);
        chat_titleBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View v) {
                exitDialog();   //确认退出弹窗
            }

            @Override
            public void onTitleClick(View v) {
//                Toast.makeText(getApplicationContext(), R.string.modify_Room, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightClick(View v) {
                Toast.makeText(getApplicationContext(), String.format(getMsg(R.string.dialog), MsgHandle.getOnline()), Toast.LENGTH_SHORT).show();
            }
        });

        lv_chatMsg = findViewById(R.id.lv_chatMsg);
        Button btn_sendMsg = findViewById(R.id.btn_sendMsg);
        et_getMsg = findViewById(R.id.et_getMsg);
        chatAdapter = new ChatAdapter(this, personChats);
        lv_chatMsg.setAdapter(chatAdapter);

        btn_sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_getMsg.getText().toString())) {
                    Toast.makeText(ChatMsgActivity.this, getMsg(R.string.not_empty), Toast.LENGTH_SHORT).show();
                }else{
                    mClickListener(et_getMsg.getText().toString());
                }
            }
        });
    }

    //发送消息
    private void mClickListener(String getMsg) {
        PersonChat personChat = new PersonChat(0,myName,getMsg,true);
        personChats.add(personChat);
        chatAdapter.notifyDataSetChanged();
        handler.sendEmptyMessage(1);
        soundPoolManager.playSingle(SoundPoolManager.SEND);
        SocketManager.sendMessage(et_getMsg.getText().toString());
        et_getMsg.setText("");
    }

    /**
     * 显示接收到的消息
     *
     * @param name   传入发信人昵称
     * @param getMsg 传入收到的聊天内容
     */
    public static void setMsg(String name, String getMsg) {
        PersonChat personChat = new PersonChat(0,name,getMsg,false);
        personChats.add(personChat);
        soundPoolManager.playSingle(SoundPoolManager.LOAD);
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
        waitingDialog.setTitle(getMsg(R.string.signOut));
        waitingDialog.setMessage(getMsg(R.string.disconnecting));
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
        exitDialog();
    }

    private void exitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getMsg(R.string.tip));
        builder.setIcon(R.mipmap.ic_launcher_round);
        builder.setMessage(getMsg(R.string.sureExit));
        //设置取消按钮
        builder.setNegativeButton(getMsg(R.string.cancel), null);
        //设置确定按钮
        builder.setPositiveButton(getMsg(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SocketManager.sendMessage("disconnect");
                dialog.dismiss();
            }
        });
        //显示弹窗
        builder.show();
    }

    private String getMsg(int stringId) {
        return getResources().getString(stringId);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: 退出ChatMsgActivity界面");
        personChats.clear();
        super.onDestroy();
    }

    @Override
    public void mHandle() {
        mHandler.sendEmptyMessage(4);
    }
}
