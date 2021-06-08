package com.pei.platformplugin;

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
    public boolean has(String key) {
        return mMap.containsKey(key);
    }
}