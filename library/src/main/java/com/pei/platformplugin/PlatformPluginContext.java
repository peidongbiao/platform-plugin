package com.pei.platformplugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Pair;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

/**
 * Created by peidongbiao on 2018/8/17.
 */
public class PlatformPluginContext {

    private Context mContext;
    private PlatformPluginHost mPluginHost;
    private PlatformPlugin mStartActivityCallback;
    private int mStartActivityRequestCode;
    private SparseArray<Pair<PlatformPlugin, Integer>> mPermissionCallback;
    private int mMappedRequestCode;


    public PlatformPluginContext(Context context, PlatformPluginHost mPluginHost) {
        this.mContext = context;
        this.mPluginHost = mPluginHost;
        mPermissionCallback = new SparseArray<>();
    }

    public void startActivity(Intent intent) {
        mPluginHost.startActivity(intent);
    }

    public void startActivityForResult(PlatformPlugin handler, Intent intent, int requestCode) {
        mPluginHost.startActivityForResult(intent, requestCode);
        mStartActivityCallback = handler;
        mStartActivityRequestCode = requestCode;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mStartActivityCallback != null && mStartActivityRequestCode == requestCode) {
            mStartActivityCallback.onActivityResult(requestCode, resultCode, data);
            mStartActivityCallback = null;
        }
    }

    public boolean checkSelfPermission(String permission) {
        return mPluginHost.checkSelfPermission(permission);
    }

    public void requestPermissions(PlatformPlugin plugin, String[] permissions, int requestCode) {
        int code = mMappedRequestCode++;
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

    public Context getContext() {
        return mContext;
    }


    interface PlatformPluginHost {

        void startActivity(Intent intent);

        void startActivityForResult(Intent intent, int requestCode);

        boolean checkSelfPermission(String permission);

        void requestPermissions(String[] permissions, int requestCode);
    }

    public static class FragmentPlatformPluginHost implements PlatformPluginHost {

        private Fragment mFragment;

        public FragmentPlatformPluginHost(Fragment fragment) {
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
    }


    public static class ActivityPlatformPluginHost implements PlatformPluginHost {

        Activity mActivity;

        public ActivityPlatformPluginHost(Activity activity) {
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
    }
}