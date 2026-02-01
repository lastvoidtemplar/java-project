package services;

import auth.AuthService;

public record Services(AuthService authService) {
    private static final String AUTH_SERVICE_NULL_MESSAGE = "Auth services must be non-null";

    public Services {
        if (authService == null) {
            throw new IllegalArgumentException(AUTH_SERVICE_NULL_MESSAGE);
        }
    }
}
