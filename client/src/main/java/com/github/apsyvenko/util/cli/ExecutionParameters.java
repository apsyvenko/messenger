package com.github.apsyvenko.util.cli;

import java.util.Map;
import java.util.TreeMap;

public class ExecutionParameters {

    private final Map<String, String> parameters = new TreeMap<>();

    public ExecutionParameters add(String key, String value) {
        if (this.parameters.containsKey(key)){
            throw new IllegalArgumentException(String.format("Parameter with a key %s is already presented.", key));
        } else {
            this.parameters.putIfAbsent(key, value);
        }
        return this;
    }

    public String getParameter(String key, String defaultValue) {
        return this.parameters.getOrDefault(key, defaultValue);
    }

    public String getParameter(Option option, String defaultValue) {
        return this.getParameter(option.getKey(), defaultValue);
    }

    public boolean isEmpty() {
        return this.parameters.isEmpty();
    }

}
