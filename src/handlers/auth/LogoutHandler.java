package handlers.auth;

import command.CommandContext;
import command.CommandHandler;
import command.ResponseWriter;

import java.util.List;

public class LogoutHandler implements CommandHandler {
    private static final String INVALID_LOGOUT_COMMAND_FORMAT =
        "Logout command doesn`t need arguments";
    private static final String WAS_LOGGED_SUCCESSFUL_MESSAGE = "Successful logout!";
    private static final String WAS_NOT_LOGGED_SUCCESSFUL_MESSAGE = "Wasn`t logged in!";

    @Override
    public void handle(ResponseWriter writer, CommandContext ctx) {
        List<String> args = ctx.cmd().commandArgs();
        if (!args.isEmpty()) {
            writer.write(INVALID_LOGOUT_COMMAND_FORMAT);
            return;
        }
        if (ctx.session().isLogged()) {
            ctx.session().logout();
            writer.write(WAS_LOGGED_SUCCESSFUL_MESSAGE);
        } else {
            writer.write(WAS_NOT_LOGGED_SUCCESSFUL_MESSAGE);
        }
    }
}
