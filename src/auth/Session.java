package auth;

public class Session {
    private static final String USERNAME_NULL_OR_BLANK_MESSAGE = "Username must be non-null non-blank string";

    private String username;

    public Session() {
        this.username = null;
    }

    public boolean isLogged() {
        return username != null;
    }

    public String getUsername() {
        return username;
    }

    public void setLoggedUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(USERNAME_NULL_OR_BLANK_MESSAGE);
        }
        this.username = username;
    }

    public void logout() {
        this.username = null;
    }
}
