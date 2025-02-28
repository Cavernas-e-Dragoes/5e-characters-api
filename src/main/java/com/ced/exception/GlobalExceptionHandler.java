package com.ced.exception;

import com.mongodb.MongoWriteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({DuplicateEmailException.class, InvalidTokenException.class, ApiException.class})
    public ResponseEntity<ErrorResponse> handleApiException(RuntimeException ex) {
        IssueType issueType;

        if (ex instanceof DuplicateEmailException) {
            issueType = ((DuplicateEmailException) ex).getIssueType();
        } else if (ex instanceof InvalidTokenException) {
            issueType = ((InvalidTokenException) ex).getIssueType();
        } else {
            issueType = ((ApiException) ex).getIssueType();
        }

        HttpStatus status = determineHttpStatus(issueType);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .withIssueType(issueType)
                .withMessage(ex.getMessage())
                .build();

        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateKeyException(DuplicateKeyException ex) {
        logger.warn("Erro de chave duplicada: {}", ex.getMessage());

        if (ex.getMessage().contains("email")) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .withIssueType(IssueType.REGISTRATION_ERROR)
                    .withMessage("Não foi possível completar o cadastro. Se já possui uma conta, tente fazer login ou usar um e-mail diferente.")
                    .build();

            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .withIssueType(IssueType.DATABASE_ERROR)
                .withMessage("Erro ao processar operação no banco de dados.")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(MongoWriteException.class)
    public ResponseEntity<ErrorResponse> handleMongoWriteException(MongoWriteException ex) {
        logger.warn("Erro de escrita MongoDB: {}", ex.getMessage());

        if (ex.getError().getCode() == 11000 && ex.getMessage().contains("email")) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .withIssueType(IssueType.REGISTRATION_ERROR)
                    .withMessage("Não foi possível completar o cadastro. Se já possui uma conta, tente fazer login ou usar um e-mail diferente.")
                    .build();

            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .withIssueType(IssueType.DATABASE_ERROR)
                .withMessage("Erro ao processar operação no banco de dados.")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> details = new HashMap<>();
        details.put("fields", errors);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .withIssueType(IssueType.VALIDATION_ERROR)
                .withMessage("Erro de validação nos campos informados.")
                .withDetails(details)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withIssueType(IssueType.AUTHENTICATION_FAILED)
                .withMessage("E-mail ou senha incorretos.")
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withIssueType(IssueType.USER_NOT_FOUND)
                .withMessage(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("Erro não tratado: ", ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .withIssueType(IssueType.SERVER_ERROR)
                .withMessage("Ocorreu um erro interno no servidor.")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }


    private HttpStatus determineHttpStatus(IssueType issueType) {
        switch (issueType) {
            case AUTHENTICATION_FAILED:
            case INVALID_TOKEN:
                return HttpStatus.UNAUTHORIZED;
            case ACCESS_DENIED:
                return HttpStatus.FORBIDDEN;
            case USER_NOT_FOUND:
            case ENTITY_NOT_FOUND:
            case RESOURCE_NOT_FOUND:
                return HttpStatus.NOT_FOUND;
            case VALIDATION_ERROR:
            case INVALID_DATA:
            case MISSING_REQUIRED_FIELD:
                return HttpStatus.BAD_REQUEST;
            case REGISTRATION_ERROR:
            case DUPLICATE_ENTITY:
                return HttpStatus.CONFLICT;
            case SERVICE_UNAVAILABLE:
                return HttpStatus.SERVICE_UNAVAILABLE;
            default:
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
