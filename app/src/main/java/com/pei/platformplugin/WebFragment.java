package com.pei.platformplugin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pei.platformplugin.dsbridge.TestPlatformInterface;

import wendu.dsbridge.DWebView;

/**
 * Created by peidongbiao on 2018/1/16.
 */

public class WebFragment extends Fragment {
    private static final String ARG_URL = "arg.url";
    private DWebView mWebView;

    private String mUrl;

    private PluginManager mPluginManager;

    public static WebFragment newInstance(String url) {

        Bundle args = new Bundle();
        args.putString(ARG_URL, url);
        WebFragment fragment = new WebFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPluginManager = new PluginManager(getActivity(), new PlatformPluginContext.FragmentPluginHost(this));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_web,container,false);
        if(mWebView != null){
            mWebView.destroy();
        }
        FrameLayout webViewContainer = view.findViewById(R.id.layout_container);
        mWebView = new DWebView(getContext());
        setWebSettings(mWebView);
        initHandlers();
        webViewContainer.addView(mWebView,new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUrl = getArguments().getString(ARG_URL);
        if (!TextUtils.isEmpty(mUrl)) {
            loadUrl(mUrl);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPluginManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPluginManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void initHandlers() {

        TestPlatformPlugin plugin  = new TestPlatformPlugin(mPluginManager.getPluginContext());
        mPluginManager.addPlugin(plugin);
        mWebView.addJavascriptObject(new TestPlatformInterface(plugin), "test");
    }

    @Override
    public void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    public void onDestroy() {
        if(mWebView != null) {
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }


    private void setWebSettings(DWebView webView){
        WebSettings webSettings = webView.getSettings();
        DWebView.setWebContentsDebuggingEnabled(true);
        // 视频播放有声音无图像问题
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        // 自动加载图片
        webSettings.setLoadsImagesAutomatically(true);
        // 控件滚动条位置
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setVerticalScrollbarOverlay(true);

        webSettings.setJavaScriptEnabled(true);
        //settings.setUseWideViewPort(false);//设定支持viewport
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true);//设定支持缩放
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false); //隐藏缩放按钮
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setAllowFileAccess(true);
        //webSettings.setAllowUniversalAccessFromFileURLs(false);
        //webSettings.setAllowFileAccessFromFileURLs(false);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setDatabaseEnabled(true);
        webSettings.setGeolocationEnabled(true);
    }

    public void loadUrl(String url){
        mWebView.loadUrl(url);
    }
}