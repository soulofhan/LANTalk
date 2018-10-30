package com.andeddo.lanchat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.andeddo.lanchat.unit.MsgHandle;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    Dialog dialog;
    long firstPressedTime = 0;
    ProgressDialog waitingDialog;
    private boolean isFocus = true;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            int what = msg.what;
            switch (what) {
                case 1:
                    if ("success".equals(MsgHandle.getSuccess())) {
                        waitingDialog.dismiss();
                        Intent intent = new Intent(MainActivity.this, ChatMsgActivity.class);
                        startActivity(intent);
                    } else if ("failure".equals(MsgHandle.getSuccess())) {
                        waitingDialog.dismiss();
                        Toast.makeText(MainActivity.this, getMsg(R.string.existed), Toast.LENGTH_SHORT).show();
                    } else {
                        mHandler.sendEmptyMessage(1);
                    }
                    break;
                case 2:
                    if (MsgHandle.getWel() && waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                        showMyDialog();
                        MsgHandle.setWel();
                    } else if ("unKnow".equals(MsgHandle.getSuccess())) {
                        waitingDialog.dismiss();
                        Toast.makeText(MainActivity.this, "连接服务器失败,请稍后重试", Toast.LENGTH_SHORT).show();
                    } else {
                        mHandler.sendEmptyMessageDelayed(2, 1000);
                    }
                    break;
                case 3:
                    Log.d(TAG, "handleMessage: MainActivity");
                    //检测服务器连接是否存在
                    if (SocketManager.getLose()) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), getMsg(R.string.lost_link), Toast.LENGTH_SHORT).show();
                    } else {
                        if (isFocus) {
                            mHandler.sendEmptyMessageDelayed(3, 1000);
                        }
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
        setContentView(R.layout.activity_main);
        Button btn_login = findViewById(R.id.btn_login);

        btn_login.setOnClickListener(myOnClickListener);
    }

    View.OnClickListener myOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_login:
                    //点击开始连接服务器
                    isFocus = true;
                    MsgHandle.setSuccess("pass");
                    showWaitingDialog(getMsg(R.string.connecting));
                    SocketManager socketManager = new SocketManager();
                    socketManager.setHost("192.168.10.129");
                    socketManager.setStatus();
                    socketManager.start();
                    mHandler.sendEmptyMessage(2);
                    break;
            }
        }
    };


    public void showMyDialog() {
        View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog, null);
        final EditText userName = dialogView.findViewById(R.id.et_userName);
        AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(this);
        mAlertDialog.setIcon(R.mipmap.ic_launcher_round);
        String title = getMsg(R.string.main_welcome);
        mAlertDialog.setTitle(title);
        mAlertDialog.setView(dialogView);
        mAlertDialog.setPositiveButton(getMsg(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String decideName = userName.getText().toString();
                if (TextUtils.isEmpty(decideName)) {
                    Toast.makeText(MainActivity.this, getMsg(R.string.nickname), Toast.LENGTH_LONG).show();
                    return;
                }
                SocketManager.sendMessage(decideName);
                showWaitingDialog(getMsg(R.string.logging));
                mHandler.sendEmptyMessage(1);
                isFocus = false; //取消handle检测掉线
                dialog.dismiss();
            }
        });

        mAlertDialog.setNegativeButton(getMsg(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SocketManager.sendMessage("disconnect");
                isFocus = false; //取消handle检测掉线
                dialog.dismiss();
            }
        });
        mAlertDialog.setCancelable(false);
        dialog = mAlertDialog.show();
        mHandler.sendEmptyMessage(3);
    }


    public void showWaitingDialog(String msg) {
        /* 等待Dialog具有屏蔽其他控件的交互能力
         * @setCancelable 为使屏幕不可点击，设置为不可取消(false)
         * 下载等事件完成后，主动调用函数关闭该Dialog
         */
        waitingDialog = new ProgressDialog(MainActivity.this);
        waitingDialog.setTitle(getMsg(R.string.response));
        waitingDialog.setMessage(msg);
        waitingDialog.setIndeterminate(true);
        waitingDialog.setCancelable(false);
        waitingDialog.create();
        waitingDialog.show();
    }

    /**
     * 获取string.xml的字符串
     *
     * @param stringId string.xml R.string.**
     * @return 返回获取到的字符串
     */
    private String getMsg(int stringId) {
        return getResources().getString(stringId);
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - firstPressedTime < 2000) {
            super.onBackPressed();
        } else {
            Toast.makeText(MainActivity.this, getMsg(R.string.again), Toast.LENGTH_SHORT).show();
            firstPressedTime = System.currentTimeMillis();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
