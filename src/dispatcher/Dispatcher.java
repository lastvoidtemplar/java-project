package dispatcher;

import command.CommandContext;
import command.CommandHandler;
import command.ResponseWriter;

import java.util.HashMap;
import java.util.Map;

public class Dispatcher {
    private final Map<String, CommandHandler> handlers;

    private Dispatcher(DispatcherBuilder builder) {
        this.handlers = builder.handlers;
    }

    public static DispatcherBuilder newBuilder() {
        return new DispatcherBuilder();
    }

    public void dispatch(ResponseWriter writer, CommandContext ctx) throws MissingHandlerException {
        String commandName = ctx.cmd().commandName();
        if (!handlers.containsKey(commandName)) {
            throw new MissingHandlerException("Handler was not register for this command");
        }
        handlers.get(commandName).handle(writer, ctx);
    }

    public static class DispatcherBuilder {
        private static final String COMMAND_NAME_NULL_OR_BLANK_MESSAGE =
            "Command name must be non-null non-blank string";
        private static final String HANDLER_NULL_MESSAGE =
            "Command handler must be non-null";

        private final Map<String, CommandHandler> handlers;

        private DispatcherBuilder() {
            this.handlers = new HashMap<>();
        }

        public DispatcherBuilder registerHandler(String commandName, CommandHandler handler) {
            if (commandName == null || commandName.isBlank()) {
                throw new IllegalArgumentException(COMMAND_NAME_NULL_OR_BLANK_MESSAGE);
            }
            if (handler == null) {
                throw new IllegalArgumentException(HANDLER_NULL_MESSAGE);
            }
            handlers.put(commandName, handler);
            return this;
        }

        public Dispatcher build() {
            return new Dispatcher(this);
        }
    }
}
