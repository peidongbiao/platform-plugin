package com.pei.platformplugin;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.pei.plaformplugin.annotation.Extra;
import com.pei.plaformplugin.annotation.Plugin;
import com.pei.plaformplugin.annotation.PluginMethod;

import java.io.File;
import java.util.UUID;

@Plugin(name = "TestPlatform")
public class TestPlatformPlugin extends PlatformPlugin {
    private static final String TAG = "TestPlatformPlugin";

    private static final int REQUEST_SELECT = 1;
    private static final int REQUEST_CAMERA = 2;
    private static final int PERMISSION_STORAGE = 1;

    private PluginCallback mChooseFileCallback;
    private PluginCallback mCaptureCallback;

    private File mCaptureFile;

    private String mType;

    public TestPlatformPlugin(PlatformPluginContext pluginContext) {
        super(pluginContext);
        pluginContext.getLifeCycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                Log.d(TAG, "onStateChanged,event: " + event.name());
            }
        });
    }

    @PluginMethod
    public void showToast(PluginArguments argument, PluginCallback callback) {
        String message = argument.get("message");
        //int duration = argument.get("duration");
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        callback.onResult(PluginResult.success(true));
    }

    @PluginMethod
    public void chooseFile(PluginArguments arguments, PluginCallback callback) {
        mChooseFileCallback = callback;
        mType = arguments.get("type");
        if (getPluginContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            doChooseFile(mType);
        } else {
            getPluginContext().requestPermissions(this, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE }, PERMISSION_STORAGE);
        }
    }

    @PluginMethod({@Extra(key = "promise", booleanValue = true)})
    public void openCamera(PluginArguments arguments, PluginCallback callback) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (mCaptureFile != null && mCaptureFile.exists()) {
            mCaptureFile.delete();
        }
        mCaptureFile = new File(getContext().getExternalCacheDir(), UUID.randomUUID().toString() + ".jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCaptureFile));
        getPluginContext().startActivityForResult(this, intent, REQUEST_CAMERA);
        this.mCaptureCallback = callback;
    }

    @PluginMethod({@Extra(key = "promise", booleanValue = true)})
    public void test(PluginArguments arguments, PluginCallback callback) {

    }

    private void doChooseFile(String type) {
        if (TextUtils.isEmpty(type)) {
            type = "*/*";
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        getPluginContext().startActivityForResult(this, intent, REQUEST_SELECT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            String filePath = FileUtils.getPathFromUri(getContext(), uri);
            //String filePath = "";
            if (mChooseFileCallback != null) {
                mChooseFileCallback.onResult(PluginResult.success(filePath));
                mChooseFileCallback = null;
            }
        } else if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            if (mCaptureCallback != null) {
                mCaptureCallback.onResult(PluginResult.success(mCaptureFile.getPath()));
            }
        }
    }

    @Override
    protected void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doChooseFile(mType);
            }
        }
    }
}