package com.pei.platformplugin.reactnative;

import com.facebook.react.bridge.Arguments;
import com.pei.platformplugin.PluginResult;

import java.util.List;
import java.util.Map;

class ReactNativeAdapter {


    protected Object convertResult(PluginResult result) {
        if (PluginResult.TYPE_MAP.equals(result.getDataType())) {
            @SuppressWarnings("unchecked")
            Map<String,Object> map = (Map<String, Object>) result.getData();
            return Arguments.makeNativeMap(map);
        } else if (PluginResult.TYPE_LIST.equals(result.getDataType())) {
            @SuppressWarnings("rawtypes")
            List list = (List) result.getData();
            return Arguments.fromList(list);
        } else {
            return result.getData();
        }
    }
}
