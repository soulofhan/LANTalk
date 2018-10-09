package com.andeddo.lanchat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.andeddo.lanchat.unit.customViewGroup;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends Activity {
    private final static String TAG = "-----MainActivity-----";
    private final static String IPAddress = "192.168.10.129";
    private final static int PORT = 5963;

    private Button btn_login;
    private EditText et_ipAddress;
    private EditText et_port;

    customViewGroup view;
    WindowManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        btn_login.setOnClickListener(myOnClickListener);
    }

    View.OnClickListener myOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_login:
                    //点击开始连接服务器
                    SocketManager socketManager = new SocketManager(getIpAddress(),getPort());
                    socketManager.start();
                    Intent intent = new Intent(MainActivity.this,ChatMsgActivity.class);
                    startActivity(intent);

                    break;

            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        disablePullNotificationTouch();
    }

    private void init() {
        btn_login = findViewById(R.id.btn_login);
        et_ipAddress = findViewById(R.id.et_ipAddress);
        et_port = findViewById(R.id.et_port);
    }

    //获取连接的ip地址
    private String getIpAddress() {
        String ipAddress = et_ipAddress.getText().toString();
        if(!TextUtils.isEmpty(ipAddress)) {
            Log.d(TAG, "getIpAddress: "+ipAddress);
            if(isIP(ipAddress)) {
                return ipAddress;
            }
        }
        return IPAddress;
    }

    //获取端口
    private int getPort() {
        String stringPort = et_port.getText().toString();
        if(!TextUtils.isEmpty(stringPort)) {
            int port = Integer.parseInt(stringPort);
            if(port<65536){
                return port;
            }
        }
        return PORT;
    }

    /**
     * 判断字符串是否为IP地址
     * @param address 传入的IP地址
     * @return 返回是否是IP地址 true或者false
     */
    public boolean isIP(String address) {
        if(address.length() < 7 || address.length() > 15 || "".equals(address)) {
            return false;
        }
        //判断IP格式和范围
        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";

        Pattern pat = Pattern.compile(rexp);

        Matcher mat = pat.matcher(address);

        return mat.find();
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
