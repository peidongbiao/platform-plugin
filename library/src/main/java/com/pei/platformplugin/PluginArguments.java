package com.pei.platformplugin;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.Map;

public interface PluginArguments {

    @Nullable
    <T> T get(String key);

    @Nullable
    <T> T get(String key, T defaultValue);

    Map<String, Object> getMap(String key);

    List<Object> getList(String key);

    boolean has(String key);
}