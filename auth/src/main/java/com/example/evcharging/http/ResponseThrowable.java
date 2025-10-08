package com.example.evcharging.http;

public class ResponseThrowable extends Exception {
    public int code;
    public String message;

    public ResponseThrowable(Throwable cause, int code) {
        super(cause);
        this.code = code;
    }
}
