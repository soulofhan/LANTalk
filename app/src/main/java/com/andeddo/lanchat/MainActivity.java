package com.andeddo.lanchat;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.andeddo.lanchat.unit.customViewGroup;


public class MainActivity extends Activity {
    private String IPAdress = "192.168.0.101";
    private int PORT = 30000;

    private Button btn_login;
    private EditText et_ipAdress;

    private String link_IP;


    customViewGroup view;
    WindowManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }

    @Override
    protected void onResume() {
        super.onResume();
        disablePullNotificationTouch();
    }

    private void init() {
        btn_login = findViewById(R.id.btn_login);
        et_ipAdress = findViewById(R.id.et_ipAdress);
    }

    private String getIpAdress() {
        return et_ipAdress.getText().toString();
    }

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
        localLayoutParams.height = (int) (25 * getResources()
                .getDisplayMetrics().scaledDensity);
        localLayoutParams.format = PixelFormat.RGBX_8888;
        view = new customViewGroup(this);
        manager.addView(view, localLayoutParams);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //允许下拉
    private void allowDropDown(){
        manager.removeView(view);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        allowDropDown();
    }
}
