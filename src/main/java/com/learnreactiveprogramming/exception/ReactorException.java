package com.learnreactiveprogramming.exception;

public class ReactorException extends Throwable {
    private final Throwable exception;
    private final String message;

    public ReactorException(Throwable exception, String message) {
        this.exception = exception;
        this.message = message;

    }
}