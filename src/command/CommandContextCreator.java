package command;

import auth.Session;
import services.Services;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CommandContextCreator {
    private static List<String> getCommandArguments(String input) {
        List<String> arguments = new ArrayList<>();
        StringBuilder builder = new StringBuilder();

        boolean insideQuote = false;
        for (char c : input.toCharArray()) {
            if (c == '"') {
                insideQuote = !insideQuote;
            }
            if (c == ' ' && !insideQuote) {
                String argument = builder.toString().replace("\"", "");
                if (!argument.isBlank()) {
                    arguments.add(argument);
                }
                builder.delete(0, builder.length());
            } else {
                builder.append(c);
            }
        }

        arguments.add(builder.toString().replace("\"", ""));
        return arguments;
    }

    private static Command newCommand(String input) {
        List<String> arguments = CommandContextCreator.getCommandArguments(input);
        List<String> commandArguments = arguments.subList(1, arguments.size());

        return new Command(arguments.getFirst(), commandArguments);
    }

    public static CommandContext newCommandContext(String input, InputStream stream, Session session,
                                                   Services services) {
        Command command = newCommand(input);
        return new CommandContext(command, stream, session, services);
    }
}
