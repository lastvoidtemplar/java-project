package command;

import java.util.List;

public record Command(String commandName, List<String> commandArgs) {
    private static final String COMMAND_NAME_NULL_OR_BLANK_MESSAGE = "Command name must be non-null non-blank string";
    private static final String COMMAND_ARGUMENTS_NULL_MESSAGE = "Command args must be non-null list";

    public Command {
        if (commandName == null || commandName.isBlank()) {
            throw new IllegalArgumentException(COMMAND_NAME_NULL_OR_BLANK_MESSAGE);
        }
        if (commandArgs == null) {
            throw new IllegalArgumentException(COMMAND_ARGUMENTS_NULL_MESSAGE);
        }
    }
}
