package auth;

public class UserInvalidPasswordException extends Exception {
    public UserInvalidPasswordException(String message) {
        super(message);
    }

    public UserInvalidPasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}
