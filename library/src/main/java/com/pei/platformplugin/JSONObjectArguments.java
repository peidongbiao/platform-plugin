package com.pei.platformplugin;

import org.json.JSONObject;

public class JSONObjectArguments implements PluginArguments {

    JSONObject mJSONObject;

    public JSONObjectArguments(JSONObject args) {
        this.mJSONObject = args;
    }

    @Override
    public <T> T get(String key) {
        @SuppressWarnings("unchecked")
        T value = (T) mJSONObject.opt(key);
        return value;
    }

    @Override
    public <T> T get(String key, T defaultValue) {
        T value = get(key);
        if (value == null) return defaultValue;
        return value;
    }

    @Override
    public boolean has(String key) {
        return mJSONObject.has(key);
    }
}
