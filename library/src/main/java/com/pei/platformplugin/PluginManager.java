package com.pei.platformplugin;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public class PluginManager {
    private static final String TAG = "PluginManager";

    private PlatformPluginContext mPluginContext;
    private Map<Class<? extends PlatformPlugin>, PlatformPlugin> mPluginMap = new HashMap<>();


    public PluginManager(Context context, PlatformPluginContext.PlatformPluginHost host) {
        mPluginContext = new PlatformPluginContext(context, this, host);
    }

    public void addPlugin(PlatformPlugin plugin) {
        if (mPluginMap.containsKey(plugin.getClass())) {
            Log.w(TAG, "addPlugin, " + plugin.getClass().getName() + " already added!");
            return;
        }
        mPluginMap.put(plugin.getClass(), plugin);
    }


    public boolean hashPlugin(Class<? extends PlatformPlugin> pluginClass) {
        return mPluginMap.containsKey(pluginClass);
    }


    @Nullable
    public <T extends PlatformPlugin> T getPlugin(Class<? extends PlatformPlugin> pluginClass) {
        @SuppressWarnings("unchecked")
        T plugin =  (T) mPluginMap.get(pluginClass);
        return plugin;
    }

    public PlatformPluginContext getPluginContext() {
        return mPluginContext;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPluginContext.onActivityResult(requestCode, resultCode, data);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPluginContext.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}