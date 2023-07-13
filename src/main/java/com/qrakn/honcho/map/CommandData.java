package com.qrakn.honcho.map;

import com.qrakn.honcho.command.CommandMeta;

public class CommandData {
    private final Object instance;

    private final CommandMeta meta;

    private final MethodData[] methodData;

    public CommandData(Object instance, CommandMeta meta, MethodData[] methodData) {
        this.instance = instance;
        this.meta = meta;
        this.methodData = methodData;
    }

    public Object getInstance() {
        return this.instance;
    }

    public CommandMeta getMeta() {
        return this.meta;
    }

    public MethodData[] getMethodData() {
        return this.methodData;
    }
}
