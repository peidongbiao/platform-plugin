package com.pei.platformplugin;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

public class PlatformPlugin {

    private PlatformPluginContext mPluginContext;


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    protected void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }

    protected void onNewIntent(Intent intent) {

    }

    public PlatformPluginContext getPluginContext() {
        return mPluginContext;
    }

    public void setPluginContext(PlatformPluginContext pluginContext) {
        mPluginContext = pluginContext;
    }

    public Context getContext() {
        return mPluginContext.getContext();
    }
}
