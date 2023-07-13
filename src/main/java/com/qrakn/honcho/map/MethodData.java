package com.qrakn.honcho.map;

import java.lang.reflect.Method;

public class MethodData {
    private final Method method;

    private final ParameterData[] parameterData;

    public MethodData(Method method, ParameterData[] parameterData) {
        this.method = method;
        this.parameterData = parameterData;
    }

    public Method getMethod() {
        return this.method;
    }

    public ParameterData[] getParameterData() {
        return this.parameterData;
    }
}
