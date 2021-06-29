package com.pei.platformplugin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    public Map<String, Object> getMap(String key) {
        if (!has(key)) return null;
        try {
            JSONObject jsonObject = mJSONObject.getJSONObject(key);
            if (jsonObject == null) return null;
            return toMap(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Object> getList(String key) {
        if (!has(key)) return null;
        try {
            JSONArray jsonObject = mJSONObject.getJSONArray(key);
            if (jsonObject == null) return null;
            return toList(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean has(String key) {
        return mJSONObject.has(key);
    }

    private Map<String,Object> toMap(JSONObject jsonObject) throws JSONException {
        if (jsonObject == null) return Collections.emptyMap();
        Map<String,Object> map = new HashMap<>();
        Iterator<String> iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = jsonObject.get(key);
            if (value instanceof JSONObject) {
                map.put(key, toMap((JSONObject) value));
            } else if (value instanceof JSONArray) {
                map.put(key, toList((JSONArray) value));
            } else {
                map.put(key, value);
            }
        }
        return map;
    }

    private List<Object> toList(JSONArray jsonArray) throws JSONException {
        if (jsonArray == null) return Collections.emptyList();
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            Object value = jsonArray.get(i);
            if (value instanceof JSONObject) {
                list.add(toMap((JSONObject) value));
            } else if (value instanceof List) {
                list.add(toList((JSONArray) value));
            } else {
                list.add(value);
            }
        }
        return list;
    }
}
