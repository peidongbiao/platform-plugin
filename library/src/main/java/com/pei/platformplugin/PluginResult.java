package com.pei.platformplugin;

import androidx.annotation.StringDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Map;

/**
 * 回调结果
 * Created by peidongbiao on 2018/8/18.
 */
public class PluginResult {
    public static final String TYPE_NULL = "null";
    public static final String TYPE_BOOLEAN = "boolean";
    public static final String TYPE_NUMBER = "number";
    public static final String TYPE_STRING = "string";
    public static final String TYPE_MAP = "boolean";
    public static final String TYPE_LIST = "list";

    @StringDef({TYPE_NULL, TYPE_BOOLEAN, TYPE_NUMBER, TYPE_STRING, TYPE_MAP, TYPE_LIST})
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
    @interface DataType{}

    public static final String ERROR_NONE = "0";

    private final Object data;

    private final boolean success;

    private final String errorCode;

    private final String errorMsg;

    @DataType
    private final String dataType;

    public static PluginResult success() {
        return new PluginResult(true, null, TYPE_NULL, ERROR_NONE, null);
    }

    public static PluginResult success(boolean data) {
        return new PluginResult(true, data, TYPE_BOOLEAN, ERROR_NONE, null);
    }

    public static PluginResult success(Number data) {
        return new PluginResult(true, data, TYPE_NUMBER, ERROR_NONE, null);
    }

    public static PluginResult success(String data) {
        return new PluginResult(true, data, TYPE_STRING, ERROR_NONE, null);
    }

    public static <T> PluginResult success(Map<String,T> data) {
        return new PluginResult(true, data, TYPE_MAP, ERROR_NONE, null);
    }

    public static <T> PluginResult success(List<T> data) {
        return new PluginResult(true, data, TYPE_LIST, ERROR_NONE, null);
    }

    public static PluginResult failure(String errorCode, String errorMsg) {
        return new PluginResult(false, null, TYPE_NULL, errorCode, errorMsg);
    }

    private PluginResult(boolean success, Object data, @DataType String type, String errorCode, String errorMsg) {
        this.data = data;
        this.success = success;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.dataType = type;
    }

    public Object getData() {
        return data;
    }

    @DataType
    public String getDataType() {
        return dataType;
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

}
