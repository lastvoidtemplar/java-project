package command;

import auth.Session;
import services.Services;

public record CommandContext(Command cmd, Session session, Services services) {
    private static final String COMMAND_NULL_MESSAGE = "Command must be non-null";
    private static final String SESSION_NULL_MESSAGE = "Session must be non-null";
    private static final String SERVICES_NULL_MESSAGE = "Services must be non-null";

    public CommandContext {
        if (cmd == null) {
            throw new IllegalArgumentException(COMMAND_NULL_MESSAGE);
        }
        if (session == null) {
            throw new IllegalArgumentException(SESSION_NULL_MESSAGE);
        }
        if (services == null) {
            throw new IllegalArgumentException(SERVICES_NULL_MESSAGE);
        }
    }
}
