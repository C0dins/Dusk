package com.qrakn.honcho.command;

public class CommandOption {
    private final String tag;

    public String getTag() {
        return this.tag;
    }

    public CommandOption(String tag) {
        this.tag = tag.toLowerCase();
    }
}
