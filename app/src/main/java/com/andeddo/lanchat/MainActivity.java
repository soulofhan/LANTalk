package com.andeddo.lanchat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.andeddo.lanchat.unit.MsgHandle;


public class MainActivity extends Activity {

    View dialogView;
    long firstPressedTime = 0;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {            // TODO Auto-generated method stub
            int what = msg.what;
            switch (what) {
                case 1:
                    TextView tv = dialogView.findViewById(R.id.tv_onLink);
                    String online = MsgHandle.getOnline();
                    tv.setText(String.format(getResources().getString(R.string.dialog), online));
                    mHandler.sendEmptyMessageDelayed(1, 1000);
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
                    SocketManager socketManager = new SocketManager();
                    socketManager.start();
                    showMyDialog();
                    mHandler.sendEmptyMessage(1);
                    break;
            }
        }
    };


    public void showMyDialog() {
        dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog, null);
        final EditText userName = dialogView.findViewById(R.id.et_userName);
        AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(this);
        mAlertDialog.setIcon(R.mipmap.ic_launcher_round);
        mAlertDialog.setTitle(getXmlString(R.string.main_welcome));
        mAlertDialog.setView(dialogView);
        mAlertDialog.setPositiveButton(getXmlString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String decideName = userName.getText().toString();
                if (TextUtils.isEmpty(decideName)) {
                    Toast.makeText(MainActivity.this, getXmlString(R.string.nickname), Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(MainActivity.this, ChatMsgActivity.class);
                startActivity(intent);
                SocketManager.sendMessage(decideName);
            }
        });

        mAlertDialog.setNegativeButton(getXmlString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mAlertDialog.setCancelable(false);
        mAlertDialog.show();
    }

    /**
     * 获取string.xml的字符串
     *
     * @param stringId string.xml R.string.**
     * @return 返回获取到的字符串
     */
    private String getXmlString(int stringId) {
        return getResources().getString(stringId);
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - firstPressedTime < 2000) {
            super.onBackPressed();
        } else {
            Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
            firstPressedTime = System.currentTimeMillis();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
