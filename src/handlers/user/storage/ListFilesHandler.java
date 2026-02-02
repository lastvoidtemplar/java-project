package handlers.user.storage;

import command.CommandContext;
import command.CommandHandler;
import command.ResponseWriter;

import java.io.IOException;
import java.util.List;

public class ListFilesHandler implements CommandHandler {
    private static final String INVALID_LIST_FILES_COMMAND_FORMAT =
        "Logout command doesn`t need arguments";
    private static final String LOGIN_FIRST_MESSAGE = "Login first!";
    private static final String SERVER_PROBLEM_MESSAGE =
        "Server problem 500";

    @Override
    public void handle(ResponseWriter writer, CommandContext ctx) {
        List<String> args = ctx.cmd().commandArgs();
        if (!args.isEmpty()) {
            writer.write(INVALID_LIST_FILES_COMMAND_FORMAT);
            return;
        }
        if (!ctx.session().isLogged()) {
            System.out.println(LOGIN_FIRST_MESSAGE);
            writer.write(LOGIN_FIRST_MESSAGE);
            return;
        }
        String loggedUsername = ctx.session().getUsername();
        try {
            List<String> filenames = ctx.services().userDirectoryService().list(loggedUsername);
            writer.write(String.join(" ", filenames));
        } catch (IOException e) {
            writer.write(SERVER_PROBLEM_MESSAGE);
        }
    }
}
