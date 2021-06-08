package com.pei.platformplugin;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.pei.plaformplugin.annotation.Plugin;
import com.pei.plaformplugin.annotation.PluginMethod;

@Plugin(name = "TestPlatform")
public class TestPlatformPlugin extends PlatformPlugin {

    private static final int REQUEST_SELECT = 1;

    private Callback<String> mChooseFileCallback;

    @PluginMethod
    public void showToast(Arguments argument, Callback<Boolean> callback) {
        String message = argument.get("message");
        int duration = argument.get("duration");
        Toast.makeText(getContext(), message, duration).show();
        callback.onResult(Result.success(true));
    }

    @PluginMethod
    public void chooseFile(Arguments arguments, Callback<String> callback) {
        mChooseFileCallback = callback;
        String type = arguments.get("type");
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
            //String filePath = FileUtils.getPathFromUri(getContext(), uri);
            String filePath = "";
            if (mChooseFileCallback != null) {
                mChooseFileCallback.onResult(Result.success(filePath));
                mChooseFileCallback = null;
            }
        }
    }
}