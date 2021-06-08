package com.pei.platformplugin.reactnative;

import com.facebook.react.bridge.Promise;
import com.pei.platformplugin.PluginCallback;
import com.pei.platformplugin.PluginResult;

public class ReactNativePromiseAdapter extends ReactNativeAdapter implements PluginCallback {

    Promise mPromise;

    public ReactNativePromiseAdapter(Promise promise) {
        mPromise = promise;
    }

    @Override
    public void onResult(PluginResult result) {
        if (mPromise == null) return;
        if (result.isSuccessful()) {
            mPromise.resolve(convertResult(result));
        } else if (!result.isSuccessful()) {
            mPromise.reject(result.getErrorCode(), result.getErrorMsg());
        }
    }
}