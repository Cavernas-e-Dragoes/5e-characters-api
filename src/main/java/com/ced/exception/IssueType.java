package com.ced.exception;


import lombok.Getter;

@Getter
public enum IssueType {

    AUTHENTICATION_FAILED("authentication_failed", "Falha na autenticação"),
    INVALID_TOKEN("invalid_token", "Token inválido ou expirado"),
    ACCESS_DENIED("access_denied", "Acesso negado a este recurso"),

    REGISTRATION_ERROR("registration_error", "Erro no processo de registro"),
    USER_NOT_FOUND("user_not_found", "Usuário não encontrado"),
    EMAIL_ALREADY_VERIFIED("email_already_verified", "E-mail já foi verificado"),
    EMAIL_VERIFICATION_FAILED("email_verification_failed", "Falha na verificação de e-mail"),

    VALIDATION_ERROR("validation_error", "Erro de validação nos dados fornecidos"),
    INVALID_DATA("invalid_data", "Dados inválidos ou malformados"),
    MISSING_REQUIRED_FIELD("missing_field", "Campo obrigatório não fornecido"),

    DATABASE_ERROR("database_error", "Erro na operação de banco de dados"),
    ENTITY_NOT_FOUND("entity_not_found", "Registro não encontrado"),
    DUPLICATE_ENTITY("duplicate_entity", "Registro duplicado"),

    SERVER_ERROR("server_error", "Erro interno do servidor"),
    SERVICE_UNAVAILABLE("service_unavailable", "Serviço temporariamente indisponível"),
    RESOURCE_NOT_FOUND("resource_not_found", "Recurso não encontrado");

    private final String code;
    private final String defaultMessage;

    IssueType(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
} 