package com.github.apsyvenko.util.cli;

import java.util.Objects;

public class CommandLineParser {

    public static ExecutionParameters parse(String[] args, Options options) {
        ExecutionParameters executionParameters = new ExecutionParameters();

        if (options.requiredCount() > args.length * 2L) {
            printUsage(options);
            return executionParameters.isEmpty() ? executionParameters : new ExecutionParameters();
        }

        for (Option option : options.getRequired()) {
            int i = indexOf(args, option.toInputKey());
            if (i == -1) {
                printUsage(options);
                return executionParameters.isEmpty() ? executionParameters : new ExecutionParameters();
            } else {
                executionParameters.add(option.getKey(), args[i + 1]);
            }
        }

        for (Option option : options.getNonRequired()) {
            int i = indexOf(args, option.toInputKey());
            if (i != -1) {
                executionParameters.add(option.getKey(), args[i + 1]);
            }
        }

        return executionParameters;
    }

    private static void printUsage(Options options) {
        System.out.println("Usage:");
        for (Option option : options.getRequired()) {
            System.out.println(option);
        }
        System.out.println("Optional parameters:");
        for (Option option : options.getNonRequired()) {
            System.out.println(option);
        }
    }

    private static int indexOf(String[] args, String str) {
        for (int i = 0; i < args.length; i++) {
            if (Objects.equals(str, args[i])){
                return i;
            }
        }
        return -1;
    }

}
