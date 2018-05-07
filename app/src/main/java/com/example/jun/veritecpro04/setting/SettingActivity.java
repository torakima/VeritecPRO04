package com.example.jun.veritecpro04.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.example.jun.veritecpro04.BaseActivity;
import com.example.jun.veritecpro04.R;

public class SettingActivity extends BaseActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_main);

        findViewById(R.id.name_change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingActivity.this, GroupRenameActivity.class));
            }
        });
        findViewById(R.id.wifi_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingActivity.this, WifiDataActivity.class));
            }
        });
    }
}
