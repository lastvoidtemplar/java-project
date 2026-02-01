package auth;

public record User(String name, String password) {
    private static final String USER_NAME_NULL_OR_BLANK_MESSAGE = "Command name must be non-null non-blank string";
    private static final String USER_PASS_NULL_OR_BLANK_MESSAGE = "Command password must be non-null non-blank string";
    private static final String LINE_NULL_OR_BLANK_MESSAGE = "Line must be non-null non-blank string";
    private static final String INVALID_SPLIT_SIZE = "Line must have the from: \"<name> <password>\"";
    private static final String SPACE = " ";
    private static final int SPLIT_SIZE = 2;
    private static final int NAME_SPLIT_INDEX = 0;
    private static final int PASSWORD_SPLIT_INDEX = 1;

    public User {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(USER_NAME_NULL_OR_BLANK_MESSAGE);
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException(USER_PASS_NULL_OR_BLANK_MESSAGE);
        }
    }

    public static User of(String line) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException(LINE_NULL_OR_BLANK_MESSAGE);
        }
        String[] split = line.split(SPACE);
        if (split.length != SPLIT_SIZE) {
            throw new IllegalArgumentException(INVALID_SPLIT_SIZE);
        }
        return new User(split[NAME_SPLIT_INDEX], split[PASSWORD_SPLIT_INDEX]);
    }
}

