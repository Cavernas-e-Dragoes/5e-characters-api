package com.ced.exception;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;


@Getter
public class ErrorResponse {
    private final boolean success;
    private String error;
    private String message;
    private final LocalDateTime timestamp;
    private Map<String, Object> details;

    private ErrorResponse() {
        this.success = false;
        this.timestamp = LocalDateTime.now();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final ErrorResponse response;

        private Builder() {
            this.response = new ErrorResponse();
        }

        public Builder withIssueType(IssueType issueType) {
            this.response.error = issueType.getCode();
            if (this.response.message == null) {
                this.response.message = issueType.getDefaultMessage();
            }
            return this;
        }

        public Builder withMessage(String message) {
            this.response.message = message;
            return this;
        }

        public Builder withDetails(Map<String, Object> details) {
            this.response.details = details;
            return this;
        }

        public ErrorResponse build() {
            return this.response;
        }
    }
} 