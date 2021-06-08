package com.pei.platformplugin;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class WebActivity extends AppCompatActivity {


    private Button mBtnLoad;
    private Button mBtnLoadSd;
    private WebFragment mWebFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_webview);
        mBtnLoad = findViewById(R.id.btn_load_asset);
        mBtnLoadSd = findViewById(R.id.btn_load_sd);

        mWebFragment = (WebFragment) getSupportFragmentManager().findFragmentById(R.id.layout_fragment_container);
        if(mWebFragment == null) {
            mWebFragment = WebFragment.newInstance("file:///android_asset/web/index.html");
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.layout_fragment_container,mWebFragment)
                    .commitAllowingStateLoss();
        }

        mBtnLoadSd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = Environment.getExternalStorageDirectory().getPath() + File.separator +  "index.html";
                mWebFragment.loadUrl(url);
            }
        });
    }
}
