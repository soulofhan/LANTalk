package com.andeddo.lanchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private String IPAdress = "192.168.0.101";
    private int PORT = 30000;

    private Button btn_login;
    private EditText et_ipAdress;

    private String link_IP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        btn_login = findViewById(R.id.btn_login);
        et_ipAdress = findViewById(R.id.et_ipAdress);
    }

    private String getIpAdress() {
        return et_ipAdress.getText().toString();
    }


}
