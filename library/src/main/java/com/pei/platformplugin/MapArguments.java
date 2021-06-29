package com.pei.platformplugin;

import java.util.List;
import java.util.Map;

public class MapArguments implements PluginArguments {

    private final Map<?,?> mMap;

    public MapArguments(Map<?,?> map) {
        mMap = map;
    }

    @Override
    public <T> T get(String key) {
        @SuppressWarnings("unchecked")
        T value = (T) mMap.get(key);
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
        @SuppressWarnings("unchecked")
        Map<String,Object> map = (Map<String, Object>) mMap.get(key);
        return map;
    }

    @Override
    public List<Object> getList(String key) {
        @SuppressWarnings("unchecked")
        List<Object> list = (List<Object>) mMap.get(key);
        return list;
    }

    @Override
    public boolean has(String key) {
        return mMap.containsKey(key);
    }
}