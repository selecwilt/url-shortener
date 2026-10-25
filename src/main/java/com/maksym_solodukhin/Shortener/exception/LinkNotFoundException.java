package com.maksym_solodukhin.Shortener.exception;

public class LinkNotFoundException extends RuntimeException {
    public LinkNotFoundException(String code) {
        super("Link with code '" + code + "' not found");
    }
}
