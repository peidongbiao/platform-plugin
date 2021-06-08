package com.pei.platformplugin;

public interface Arguments {

    <T> T get(String key);

    boolean has(String key);
}