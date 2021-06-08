package com.pei.platformplugin.flutter;

import com.pei.platformplugin.PluginCallback;
import com.pei.platformplugin.PluginResult;

import io.flutter.plugin.common.MethodChannel;

public class MethodChannelResultCallbackAdapter implements PluginCallback {

    MethodChannel.Result mResult;

    public MethodChannelResultCallbackAdapter(MethodChannel.Result result) {
        mResult = result;
    }

    @Override
    public void onResult(PluginResult result) {
        if (result.isSuccessful()) {
            mResult.success(result.getData());
        } else {
            mResult.error(result.getErrorCode(), result.getErrorMsg(), null);
        }
    }
}