package com.ian.alog.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ubtrobot.alog.ALog;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ALog.d("onCreate");
    }
}
