package com.pei.platformplugin.reactnative;


import com.facebook.react.bridge.Callback;
import com.pei.platformplugin.PluginCallback;
import com.pei.platformplugin.PluginResult;

public class ReactNativeCallbackAdapter extends ReactNativeAdapter implements PluginCallback {

    Callback mSuccess;
    Callback mFailure;

    public ReactNativeCallbackAdapter(Callback success, Callback failure) {
        mSuccess = success;
        mFailure = failure;
    }

    @Override
    public void onResult(PluginResult result) {
        if (result.isSuccessful() && mSuccess != null) {
            mSuccess.invoke(convertResult(result));
        } else if (!result.isSuccessful() && mFailure != null) {
            mFailure.invoke(result.getErrorCode(), result.getErrorMsg());
        }
    }
}