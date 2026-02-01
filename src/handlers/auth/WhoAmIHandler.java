package handlers.auth;

import command.CommandContext;
import command.CommandHandler;
import command.ResponseWriter;

import java.util.Formatter;
import java.util.List;

public class WhoAmIHandler implements CommandHandler {
    private static final String INVALID_WHO_AM_I_COMMAND_FORMAT =
        "Logout command doesn`t need arguments";
    private static final String HELLO_FORMAT = "Hello, %s!";
    private static final String LOGIN_FIRST_MESSAGE = "Login first!";

    @Override
    public void handle(ResponseWriter writer, CommandContext ctx) {
        List<String> args = ctx.cmd().commandArgs();
        if (!args.isEmpty()) {
            writer.write(INVALID_WHO_AM_I_COMMAND_FORMAT);
            return;
        }

        if (ctx.session().isLogged()) {
            Formatter fmt = new Formatter();
            String msg = fmt.format(HELLO_FORMAT, ctx.session().getUsername()).toString();
            writer.write(msg);
        } else {
            writer.write(LOGIN_FIRST_MESSAGE);
        }
    }
}
