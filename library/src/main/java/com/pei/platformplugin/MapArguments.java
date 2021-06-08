package com.pei.platformplugin;

import java.util.Map;

public class MapArguments implements Arguments {

    private Map mMap;

    public MapArguments(Map map) {
        mMap = map;
    }

    @Override
    public <T> T get(String key) {
        return (T) mMap.get(key);
    }

    @Override
    public boolean has(String key) {
        return mMap.containsKey(key);
    }
}
