package com.pei.platformplugin;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

public class PlatformPlugin {

    private PlatformPluginContext mPluginContext;

    public PlatformPlugin(PlatformPluginContext pluginContext) {
        mPluginContext = pluginContext;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    protected void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }

    public PlatformPluginContext getPluginContext() {
        return mPluginContext;
    }

    public Context getContext() {
        return mPluginContext.getApplicationContext();
    }
}
