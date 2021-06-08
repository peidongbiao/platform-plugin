package com.pei.platformplugin.dsbridge;

import com.pei.platformplugin.Callback;
import com.pei.platformplugin.Result;

import wendu.dsbridge.CompletionHandler;

public class CompletionHandlerCallbackAdapter<T> implements Callback<T> {

    CompletionHandler<Result<T>> mCompletionHandler;

    public CompletionHandlerCallbackAdapter(CompletionHandler<Result<T>> completionHandler) {
        mCompletionHandler = completionHandler;
    }

    @Override
    public void onResult(Result<T> result) {
        mCompletionHandler.complete(result);
    }
}