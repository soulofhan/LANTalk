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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.andeddo.lanchat.unit.MsgHandle;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    View dialogView;
    long firstPressedTime = 0;
    ProgressDialog waitingDialog;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {            // TODO Auto-generated method stub
            int what = msg.what;
            switch (what) {
                case 1:
                    if ("success".equals(MsgHandle.getSuccess())) {
                        waitingDialog.dismiss();
                        Intent intent = new Intent(MainActivity.this, ChatMsgActivity.class);
                        startActivity(intent);
                    } else if ("failure".equals(MsgHandle.getSuccess())) {
                        waitingDialog.dismiss();
                        Toast.makeText(MainActivity.this, getXmlString(R.string.existed), Toast.LENGTH_SHORT).show();
                    } else {
                        mHandler.sendEmptyMessage(1);
                    }
                    break;
                case 2:
                    boolean wel = MsgHandle.getWel();
                    if (wel && waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                        showMyDialog();
                        MsgHandle.setWel();
                    } else {
                        mHandler.sendEmptyMessageDelayed(2, 1000);
                    }
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
                    showWaitingDialog("正在连接服务器......");
                    SocketManager.setStatus();
                    SocketManager socketManager = new SocketManager();
                    socketManager.start();
                    mHandler.sendEmptyMessage(2);
                    break;
            }
        }
    };


    public void showMyDialog() {
        dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog, null);
        final EditText userName = dialogView.findViewById(R.id.et_userName);
        AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(this);
        mAlertDialog.setIcon(R.mipmap.ic_launcher_round);
        String title = getXmlString(R.string.main_welcome);
        mAlertDialog.setTitle(title);
        mAlertDialog.setView(dialogView);
        mAlertDialog.setPositiveButton(getXmlString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String decideName = userName.getText().toString();
                if (TextUtils.isEmpty(decideName)) {
                    Toast.makeText(MainActivity.this, getXmlString(R.string.nickname), Toast.LENGTH_LONG).show();
                    return;
                }
                SocketManager.sendMessage(decideName);
                showWaitingDialog("正在登陆服务器......");
                mHandler.sendEmptyMessage(1);
                dialog.dismiss();
            }
        });

        mAlertDialog.setNegativeButton(getXmlString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SocketManager.sendMessage("disconnect");
                dialog.dismiss();
            }
        });
        mAlertDialog.setCancelable(false);
        mAlertDialog.create();
        mAlertDialog.show();
    }

    public void showWaitingDialog(String msg) {
        /* 等待Dialog具有屏蔽其他控件的交互能力
         * @setCancelable 为使屏幕不可点击，设置为不可取消(false)
         * 下载等事件完成后，主动调用函数关闭该Dialog
         */
        waitingDialog = new ProgressDialog(MainActivity.this);
        waitingDialog.setTitle("等待服务器回应");
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
    private String getXmlString(int stringId) {
        String value = getResources().getString(stringId);
        Log.d(TAG, "getXmlString: " + value);
        return value;
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - firstPressedTime < 2000) {
            super.onBackPressed();
        } else {
            Toast.makeText(MainActivity.this, getXmlString(R.string.again), Toast.LENGTH_SHORT).show();
            firstPressedTime = System.currentTimeMillis();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
