package com.pei.platformplugin;

import androidx.annotation.Nullable;

public interface PluginArguments {

    @Nullable
    <T> T get(String key);

    @Nullable
    <T> T get(String key, T defaultValue);

    boolean has(String key);
}