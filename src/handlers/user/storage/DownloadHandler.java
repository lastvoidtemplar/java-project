package handlers.user.storage;

import command.CommandContext;
import command.CommandHandler;
import command.ResponseWriter;
import user.storage.DownloadResult;
import user.storage.FileNotFoundException;

import java.io.IOException;
import java.util.List;

public class DownloadHandler implements CommandHandler {
    private static final String INVALID_DOWNLOAD_COMMAND_FORMAT =
        "upload command must have 1 string arguments(<filename>)";
    private static final String LOGIN_FIRST_MESSAGE = "Login first!";
    private static final String SERVER_PROBLEM_MESSAGE =
        "Server problem 500";
    private static final String SUCCESSFUL_MESSAGE = "correct";
    private static final String ERROR_MESSAGE = "error  ";
    private static final int DOWNLOAD_ARGUMENT_COUNT = 1;
    private static final int ARGUMENTS_FILENAME_IND = 0;

    @Override
    public void handle(ResponseWriter writer, CommandContext ctx) {
        List<String> args = ctx.cmd().commandArgs();
        if (args.size() != DOWNLOAD_ARGUMENT_COUNT) {
            writer.write(ERROR_MESSAGE);
            writer.write(INVALID_DOWNLOAD_COMMAND_FORMAT);
            return;
        }
        if (!ctx.session().isLogged()) {
            writer.write(ERROR_MESSAGE);
            writer.write(LOGIN_FIRST_MESSAGE);
            return;
        }
        String loggedUsername = ctx.session().getUsername();
        try {
            DownloadResult result = ctx.services().userDirectoryService()
                .download(loggedUsername, args.get(ARGUMENTS_FILENAME_IND));
            writer.write(SUCCESSFUL_MESSAGE);
            writer.write(result.fileSize(), result.fileStream());
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            writer.write(ERROR_MESSAGE);
            writer.write(e.getMessage());
        } catch (IOException e) {
            writer.write(ERROR_MESSAGE);
            writer.write(SERVER_PROBLEM_MESSAGE);
        }
    }
}
