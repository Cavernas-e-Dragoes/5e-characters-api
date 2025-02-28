package com.ced.exception;


import lombok.Getter;

@Getter
public class InvalidTokenException extends RuntimeException {

    private final IssueType issueType;

    public InvalidTokenException(String message) {
        super(message);
        this.issueType = IssueType.INVALID_TOKEN;
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
        this.issueType = IssueType.INVALID_TOKEN;
    }

}
