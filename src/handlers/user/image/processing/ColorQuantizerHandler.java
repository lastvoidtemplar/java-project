package handlers.user.image.processing;

import command.CommandContext;
import command.CommandHandler;
import command.ResponseWriter;
import image.processing.ImageColorQuantizer;
import user.storage.FileNotFoundException;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public class ColorQuantizerHandler implements CommandHandler {
    private static final String INVALID_COLOR_QUANTIZER_COMMAND_FORMAT =
        "Color Quantizer command must have 2 arguments(<filename> <k>)";
    private static final String LOGIN_FIRST_MESSAGE = "Login first!";
    private static final String SERVER_PROBLEM_MESSAGE =
        "Server problem 500";
    private static final String SUCCESSFUL_MESSAGE = "Successful processing!";
    private static final int DELETE_ARGUMENT_COUNT = 2;
    private static final int ARGUMENTS_FILENAME_IND = 0;
    private static final int ARGUMENTS_K_IND = 1;

    @Override
    public void handle(ResponseWriter writer, CommandContext ctx) {
        List<String> args = ctx.cmd().commandArgs();
        if (!validArguments(args, writer)) {
            return;
        }
        String filename = args.get(ARGUMENTS_FILENAME_IND);
        int k = Integer.parseInt(args.get(ARGUMENTS_K_IND));
        if (!ctx.session().isLogged()) {
            writer.write(LOGIN_FIRST_MESSAGE);
            return;
        }
        String loggedUsername = ctx.session().getUsername();
        try {
            BufferedImage source = ctx.services().userDirectoryService().loadImage(loggedUsername, filename);
            BufferedImage result = new ImageColorQuantizer().colorQuantize(source, k);
            String resultFilename = generateFilenameForResultImage(filename, k);
            writer.write(SUCCESSFUL_MESSAGE);
            ctx.services().userDirectoryService().saveImage(loggedUsername, resultFilename, result);
        } catch (FileNotFoundException e) {
            writer.write(e.getMessage());
        } catch (IOException e) {
            writer.write(SERVER_PROBLEM_MESSAGE);
        }
    }

    private boolean validArguments(List<String> args, ResponseWriter writer) {
        if (args.size() != DELETE_ARGUMENT_COUNT) {
            writer.write(INVALID_COLOR_QUANTIZER_COMMAND_FORMAT);
            return false;
        }
        try {
            Integer.parseInt(args.get(ARGUMENTS_K_IND));
            return true;
        } catch (NumberFormatException e) {
            writer.write(INVALID_COLOR_QUANTIZER_COMMAND_FORMAT);
            return false;
        }
    }

    private String generateFilenameForResultImage(String filename, int k) {
        int dotIndex = filename.lastIndexOf('.');
        String name = filename.substring(0, dotIndex);
        String extension = filename.substring(dotIndex);
        return name + "_k" + k + extension;
    }
}
