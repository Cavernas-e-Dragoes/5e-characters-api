package com.ced.exception;


import lombok.Getter;

@Getter
public class DuplicateEmailException extends RuntimeException {

    private final IssueType issueType;

    public DuplicateEmailException(String email) {
        super("Não foi possível completar o cadastro. Se já possui uma conta, tente fazer login ou usar um e-mail diferente.");
        this.issueType = IssueType.REGISTRATION_ERROR;
    }

    public DuplicateEmailException(String email, Throwable cause) {
        super("Não foi possível completar o cadastro. Se já possui uma conta, tente fazer login ou usar um e-mail diferente.", cause);
        this.issueType = IssueType.REGISTRATION_ERROR;
    }

} 