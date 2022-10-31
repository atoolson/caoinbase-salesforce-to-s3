package com.andrewtoolson.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BadAccessTokenException extends RuntimeException {
    private final String errors;
}
