package com.github.apsyvenko.util.cli;

import java.util.Objects;

public class Option implements Comparable<Option> {

    private final String key;
    private final boolean required;
    private final String description;

    public Option(String key, boolean required, String description) {
        this.key = key;
        this.required = required;
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public boolean isRequired() {
        return required;
    }

    public String getDescription() {
        return description;
    }

    public String toInputKey() {
        return String.format("-%s", this.key);
    }

    @Override
    public String toString() {
        return String.format("-%-10s <arg> \t\t%s", this.key, this.description);
    }

    @Override
    public int compareTo(Option o) {
        return Objects.compare(this.key, o.key, String::compareTo);
    }

}
