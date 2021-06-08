package com.pei.platformplugin.dsbridge;

import com.pei.platformplugin.PluginCallback;
import com.pei.platformplugin.PluginResult;

import org.json.JSONException;
import org.json.JSONObject;

import wendu.dsbridge.CompletionHandler;

public class CompletionHandlerCallbackAdapter implements PluginCallback {

    CompletionHandler<JSONObject> mCompletionHandler;

    public CompletionHandlerCallbackAdapter(CompletionHandler<JSONObject> completionHandler) {
        mCompletionHandler = completionHandler;
    }

    @Override
    public void onResult(PluginResult result) {
        JSONObject object = new JSONObject();
        try {
            object.put("errorCode", result.getErrorCode());
            object.put("errorMsg", result.getErrorMsg());
            object.put("success", result.isSuccessful());
            object.put("data", result.getData());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mCompletionHandler.complete(object);
    }
}