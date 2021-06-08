package com.pei.platformplugin;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONObjectArguments implements Arguments {

    JSONObject mJSONObject;

    public JSONObjectArguments(JSONObject args) {
        this.mJSONObject = args;
    }

    @Override
    public <T> T get(String key) {
        try {
            Object value =  mJSONObject.get(key);
            return (T) value;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean has(String key) {
        return mJSONObject.has(key);
    }
}
