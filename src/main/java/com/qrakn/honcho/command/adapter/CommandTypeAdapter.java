package com.qrakn.honcho.command.adapter;

import java.util.ArrayList;
import java.util.List;

public interface CommandTypeAdapter {
    <T> T convert(String paramString, Class<T> paramClass);

    default <T> List<String> tabComplete(String string, Class<T> type) {
        return new ArrayList<>();
    }
}
