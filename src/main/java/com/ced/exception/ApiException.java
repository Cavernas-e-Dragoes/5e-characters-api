package com.ced.exception;


import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private final IssueType issueType;

    public ApiException(IssueType issueType) {
        super(issueType.getDefaultMessage());
        this.issueType = issueType;
    }

    public ApiException(IssueType issueType, String message) {
        super(message);
        this.issueType = issueType;
    }

    public ApiException(IssueType issueType, String message, Throwable cause) {
        super(message, cause);
        this.issueType = issueType;
    }

} 