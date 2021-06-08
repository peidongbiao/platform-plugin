package com.pei.platformplugin;


/**
 * 回调
 * Created by peidongbiao on 2018/8/17.
 */
public interface Callback<T> {

    void onResult(Result<T> result);
}