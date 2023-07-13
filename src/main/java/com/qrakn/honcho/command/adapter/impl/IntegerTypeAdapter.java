package com.qrakn.honcho.command.adapter.impl;

import com.qrakn.honcho.command.adapter.CommandTypeAdapter;

public class IntegerTypeAdapter implements CommandTypeAdapter {
    public <T> T convert(String string, Class<T> type) {
        return type.cast(Integer.valueOf(Integer.parseInt(string)));
    }
}
