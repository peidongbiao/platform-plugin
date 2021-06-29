package com.pei.platformplugin;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Pair;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;

/**
 * Created by peidongbiao on 2018/8/17.
 */
public class PlatformPluginContext {

    private PluginManager mPluginManager;

    private Context mContext;

    private PlatformPluginHost mPluginHost;

    private SparseArray<Pair<PlatformPlugin, Integer>> mStartActivityCallback;

    private int mStartActivityRequestCode = 10000;

    private SparseArray<Pair<PlatformPlugin, Integer>> mPermissionCallback;

    private int mPermissionRequestCode = 20000;


    public PlatformPluginContext(Context context, PluginManager pluginManager, PlatformPluginHost mPluginHost) {
        this.mContext = context.getApplicationContext();
        this.mPluginManager = pluginManager;
        this.mPluginHost = mPluginHost;
        mStartActivityCallback = new SparseArray<>();
        mPermissionCallback = new SparseArray<>();
    }

    public void startActivity(Intent intent) {
        mPluginHost.startActivity(intent);
    }

    public void startActivityForResult(PlatformPlugin plugin, Intent intent, int requestCode) {
        int code = mStartActivityRequestCode++;
        mStartActivityCallback.put(code, Pair.create(plugin, requestCode));
        mPluginHost.startActivityForResult(intent, code);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Pair<PlatformPlugin, Integer> pair = mStartActivityCallback.get(requestCode);
        if (pair != null) {
            mStartActivityCallback.delete(requestCode);
            pair.first.onActivityResult(pair.second, resultCode, data);
        }
    }

    public boolean checkSelfPermission(String permission) {
        return mPluginHost.checkSelfPermission(permission);
    }

    public void requestPermissions(PlatformPlugin plugin, String[] permissions, int requestCode) {
        int code = mPermissionRequestCode++;
        mPermissionCallback.put(code, Pair.create(plugin, requestCode));
        mPluginHost.requestPermissions(permissions, code);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Pair<PlatformPlugin, Integer> pair = mPermissionCallback.get(requestCode);
        if (pair != null) {
            mPermissionCallback.delete(requestCode);
            pair.first.onRequestPermissionsResult(pair.second, permissions, grantResults);
        }
    }

    public Context getApplicationContext() {
        return mContext;
    }

    public boolean hashPlugin(Class<? extends PlatformPlugin> pluginClass) {
        return mPluginManager.hashPlugin(pluginClass);
    }

    @Nullable
    public <T extends PlatformPlugin> T getPlugin(Class<? extends PlatformPlugin> pluginClass) {
        @SuppressWarnings("unchecked")
        T plugin =  (T) mPluginManager.getPlugin(pluginClass);
        return plugin;
    }

    public Lifecycle getLifeCycle() {
        return mPluginHost.getLifeCycle();
    }

    interface PlatformPluginHost {

        void startActivity(Intent intent);

        void startActivityForResult(Intent intent, int requestCode);

        boolean checkSelfPermission(String permission);

        void requestPermissions(String[] permissions, int requestCode);

        Lifecycle getLifeCycle();
    }

    public static class FragmentPluginHost implements PlatformPluginHost {

        private Fragment mFragment;

        public FragmentPluginHost(Fragment fragment) {
            mFragment = fragment;
        }

        @Override
        public void startActivity(Intent intent) {
            mFragment.startActivity(intent);
        }

        @Override
        public void startActivityForResult(Intent intent, int requestCode) {
            mFragment.startActivityForResult(intent, requestCode);
        }

        @Override
        public boolean checkSelfPermission(String permission) {
            return ContextCompat.checkSelfPermission(mFragment.getContext(), permission) == PackageManager.PERMISSION_GRANTED;
        }

        @Override
        public void requestPermissions(String[] permissions, int requestCode) {
            mFragment.requestPermissions(permissions, requestCode);
        }

        @Override
        public Lifecycle getLifeCycle() {
            return mFragment.getLifecycle();
        }
    }


    public static class ActivityPluginHost implements PlatformPluginHost {

        FragmentActivity mActivity;

        public ActivityPluginHost(FragmentActivity activity) {
            mActivity = activity;
        }

        @Override
        public void startActivity(Intent intent) {
            mActivity.startActivity(intent);
        }

        @Override
        public void startActivityForResult(Intent intent, int requestCode) {
            mActivity.startActivityForResult(intent, requestCode);
        }

        @Override
        public boolean checkSelfPermission(String permission) {
            return ContextCompat.checkSelfPermission(mActivity, permission) == PackageManager.PERMISSION_GRANTED;
        }

        @Override
        public void requestPermissions(String[] permissions, int requestCode) {
            ActivityCompat.requestPermissions(mActivity, permissions, requestCode);
        }

        @Override
        public Lifecycle getLifeCycle() {
            return mActivity.getLifecycle();
        }
    }
}