package services;

import auth.AuthService;
import user.storage.UserDirectoryService;

public record Services(AuthService authService, UserDirectoryService userDirectoryService) {
    private static final String AUTH_SERVICE_NULL_MESSAGE = "Auth services must be non-null";
    private static final String USER_DIRECTORY_SERVICE_NULL_MESSAGE = "User directory services must be non-null";

    public Services {
        if (authService == null) {
            throw new IllegalArgumentException(AUTH_SERVICE_NULL_MESSAGE);
        }
        if (userDirectoryService == null) {
            throw new IllegalArgumentException(USER_DIRECTORY_SERVICE_NULL_MESSAGE);
        }
    }
}
