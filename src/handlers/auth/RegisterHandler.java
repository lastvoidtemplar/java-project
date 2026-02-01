package handlers.auth;

import auth.User;
import auth.UserAlreadyExistsException;
import auth.UserPersistenceException;
import command.CommandContext;
import command.CommandHandler;
import command.ResponseWriter;

import java.util.List;

public class RegisterHandler implements CommandHandler {
    private static final String INVALID_REGISTER_COMMAND_FORMAT =
        "Register command must have 2 string arguments(<username> <password>)";
    private static final String SERVER_PROBLEM_MESSAGE =
        "Server problem 500";
    private static final String SUCCESSFUL_MESSAGE = "Successful register!";
    private static final int REGISTER_ARGUMENT_COUNT = 2;
    private static final int ARGUMENTS_USERNAME_IND = 0;
    private static final int ARGUMENTS_PASSWORD_IND = 1;

    @Override
    public void handle(ResponseWriter writer, CommandContext ctx) {
        List<String> args = ctx.cmd().commandArgs();
        if (args.size() != REGISTER_ARGUMENT_COUNT) {
            writer.write(INVALID_REGISTER_COMMAND_FORMAT);
            return;
        }
        User user;
        try {
            user = new User(args.get(ARGUMENTS_USERNAME_IND), args.get(ARGUMENTS_PASSWORD_IND));
        } catch (Exception e) {
            writer.write(INVALID_REGISTER_COMMAND_FORMAT);
            System.out.println(e.getMessage());
            return;
        }
        try {
            ctx.services().authService().register(user);
            writer.write(SUCCESSFUL_MESSAGE);
        } catch (UserAlreadyExistsException e) {
            writer.write(e.getMessage());
        } catch (UserPersistenceException e) {
            writer.write(SERVER_PROBLEM_MESSAGE);
            System.out.println(e.getMessage());

        }
    }
}
