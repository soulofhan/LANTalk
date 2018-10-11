package com.andeddo.lanchat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.andeddo.lanchat.unit.MsgHandle;
import com.andeddo.lanchat.unit.customViewGroup;


public class MainActivity extends Activity {

    View dialogView;
    customViewGroup view;
    WindowManager manager;

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
    protected void onResume() {
        super.onResume();
        disablePullNotificationTouch();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //自动隐藏通知栏与虚拟按键
        if (hasFocus && Build.VERSION.SDK_INT >= 22) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    //禁止通知栏下拉
    private void disablePullNotificationTouch() {
        manager = ((WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE));
        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |

                // this is to enable the notification to recieve touch events
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |

                // Draws over status bar
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        localLayoutParams.height = (int) (26 * getResources()
                .getDisplayMetrics().scaledDensity);
        localLayoutParams.format = PixelFormat.RGBX_8888;
        view = new customViewGroup(this);
        view.setBackgroundColor(getResources().getColor(R.color.White));
        manager.addView(view, localLayoutParams);
    }

    //允许下拉
    private void allowDropDown() {
        manager.removeView(view);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        allowDropDown();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
