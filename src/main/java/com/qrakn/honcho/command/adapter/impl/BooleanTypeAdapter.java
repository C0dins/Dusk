package com.qrakn.honcho.command.adapter.impl;

import com.qrakn.honcho.command.adapter.CommandTypeAdapter;
import java.util.HashMap;
import java.util.Map;

public class BooleanTypeAdapter implements CommandTypeAdapter {
    private static final Map<String, Boolean> MAP = new HashMap<>();

    static {
        MAP.put("true", Boolean.valueOf(true));
        MAP.put("yes", Boolean.valueOf(true));
        MAP.put("false", Boolean.valueOf(false));
        MAP.put("no", Boolean.valueOf(false));
    }

    public <T> T convert(String string, Class<T> type) {
        return type.cast(MAP.get(string.toLowerCase()));
    }
}
