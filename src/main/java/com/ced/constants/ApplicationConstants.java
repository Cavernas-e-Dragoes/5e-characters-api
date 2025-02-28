package com.ced.constants;

public class ApplicationConstants {

    private ApplicationConstants() {
    }

    // JWT and Authorization
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String JWT_SUBJECT_CLAIM = "sub";
    public static final String AUTHENTICATED_EMAIL = "authenticatedEmail";


    // Content-Type
    public static final String CONTENT_TYPE_JSON = "application/json";

    // User constants
    public static final String USER_IDENTIFIER = "user";
    public static final String TOKEN_IDENTIFIER = "token";

    // Collections
    public static final String PERSONAGENS = "personagens";
    public static final String USERS = "users";

    // Endpoints base paths
    public static final String BASE_API_PERSONAGENS = "/api/personagens";
    public static final String BASE_API_USER = "/api/user";

    public static final String TEXT_HTML = "text/html";

}