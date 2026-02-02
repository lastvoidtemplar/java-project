package handlers.user.storage;

import command.CommandContext;
import command.CommandHandler;
import command.ResponseWriter;
import user.storage.FileAlreadyExistsException;

import java.io.IOException;
import java.util.List;

public class UploadHandler implements CommandHandler {
    private static final String INVALID_UPLOAD_COMMAND_FORMAT =
        "upload command must have 1 string arguments(<filename>)";
    private static final String LOGIN_FIRST_MESSAGE = "Login first!";
    private static final String SERVER_PROBLEM_MESSAGE =
        "Server problem 500";
    private static final int UPLOAD_ARGUMENT_COUNT = 1;
    private static final int ARGUMENTS_FILENAME_IND = 0;

    @Override
    public void handle(ResponseWriter writer, CommandContext ctx) {
        List<String> args = ctx.cmd().commandArgs();
        if (args.size() != UPLOAD_ARGUMENT_COUNT) {
            writer.write(INVALID_UPLOAD_COMMAND_FORMAT);
            return;
        }
        if (!ctx.session().isLogged()) {
            System.out.println(LOGIN_FIRST_MESSAGE);
            writer.write(LOGIN_FIRST_MESSAGE);
            return;
        }
        String loggedUsername = ctx.session().getUsername();
        try {
            ctx.services().userDirectoryService()
                .upload(loggedUsername, args.get(ARGUMENTS_FILENAME_IND), ctx.stream());
        } catch (FileAlreadyExistsException e) {
            System.out.println(e.getMessage());
            writer.write(e.getMessage());
        } catch (IOException e) {
            writer.write(SERVER_PROBLEM_MESSAGE);
        }
    }
}
