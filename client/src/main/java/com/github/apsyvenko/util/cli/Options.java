package com.github.apsyvenko.util.cli;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Options {

    private final Set<Option> options = new TreeSet<>();

    public Options addOption(Option option) {
        boolean added = this.options.add(option);
        if (!added) {
            throw new IllegalArgumentException(String.format("Option with a key %s is already presented.", option.getKey()));
        }
        return this;
    }

    public long requiredCount() {
        return options.stream()
                .filter(Option::isRequired)
                .count();
    }

    public Set<Option> getRequired() {
        return options.stream()
                .filter(Option::isRequired)
                .collect(Collectors.toSet());
    }

    public Set<Option> getNonRequired() {
        return options.stream()
                .filter(option -> !option.isRequired())
                .collect(Collectors.toSet());
    }

}
