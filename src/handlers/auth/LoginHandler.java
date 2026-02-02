package handlers.auth;

import auth.User;
import auth.UserInvalidPasswordException;
import auth.UserNotFoundException;
import command.CommandContext;
import command.CommandHandler;
import command.ResponseWriter;

import java.util.List;

public class LoginHandler implements CommandHandler {
    private static final String INVALID_LOGIN_COMMAND_FORMAT =
        "Login command must have 2 string arguments(<username> <password>)";
    private static final String SUCCESSFUL_MESSAGE = "Successful login!";
    private static final int LOGIN_ARGUMENT_COUNT = 2;
    private static final int ARGUMENTS_USERNAME_IND = 0;
    private static final int ARGUMENTS_PASSWORD_IND = 1;

    @Override
    public void handle(ResponseWriter writer, CommandContext ctx) {
        List<String> args = ctx.cmd().commandArgs();
        if (args.size() != LOGIN_ARGUMENT_COUNT) {
            writer.write(INVALID_LOGIN_COMMAND_FORMAT);
            return;
        }
        User user;
        try {
            user = new User(args.get(ARGUMENTS_USERNAME_IND), args.get(ARGUMENTS_PASSWORD_IND));
        } catch (Exception e) {
            writer.write(INVALID_LOGIN_COMMAND_FORMAT);
            System.out.println(e.getMessage());
            return;
        }
        try {
            ctx.services().authService().login(user);
            ctx.session().setLoggedUsername(user.name());
            writer.write(SUCCESSFUL_MESSAGE);
        } catch (UserNotFoundException | UserInvalidPasswordException e) {
            writer.write(e.getMessage());
        }
    }
}
