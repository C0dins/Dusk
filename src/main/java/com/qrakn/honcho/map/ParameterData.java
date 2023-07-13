package com.qrakn.honcho.map;

import com.qrakn.honcho.command.CPL;

public class ParameterData {
    private final String name;

    private final Class type;

    private final CPL cpl;

    public ParameterData(String name, Class type, CPL cpl) {
        this.name = name;
        this.type = type;
        this.cpl = cpl;
    }

    public String getName() {
        return this.name;
    }

    public Class getType() {
        return this.type;
    }

    public CPL getCpl() {
        return this.cpl;
    }
}
