package com.pei.platformplugin;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 回调结果
 * Created by peidongbiao on 2018/8/18.
 */
public class Result<T> {

    public static final String ERROR_NONE = "0";

    private final T data;
    private final boolean success;
    private final String errorCode;
    private final String errorMsg;

    public static <T> Result<T> success(T data) {
        return new Result<T>(true, data, ERROR_NONE, null);
    }

    public static Result<Void> failure(String errorCode, String errorMsg) {
        return new Result<>(false, null, errorCode, errorMsg);
    }

    private Result(boolean success, T data, String errorCode, String errorMsg) {
        this.data = data;
        this.success = success;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public T getData() {
        return data;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public boolean isSuccessful() {
        return success;
    }

    @NonNull
    @Override
    public String toString() {
        try {
            JSONObject object = new JSONObject();
            object.put("success", success);
            object.put("errorCode", errorCode);
            object.put("errorMsg", errorMsg);
            object.put("data", data);
            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return super.toString();
    }
}
