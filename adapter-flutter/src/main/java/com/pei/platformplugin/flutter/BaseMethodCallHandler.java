package com.pei.platformplugin.flutter;

import com.pei.platformplugin.JSONObjectArguments;
import com.pei.platformplugin.MapArguments;
import com.pei.platformplugin.PluginArguments;

import org.json.JSONObject;

import java.util.Map;

import io.flutter.plugin.common.MethodChannel;

public abstract class BaseMethodCallHandler implements MethodChannel.MethodCallHandler {

    protected PluginArguments toArguments(Object args) {
        PluginArguments arguments;
        if (args instanceof Map) {
            arguments = new MapArguments((Map<?,?>) args);
        } else if (args instanceof JSONObject) {
            arguments = new JSONObjectArguments((JSONObject) args);
        } else {
            throw new ClassCastException();
        }
        return arguments;
    }
}
