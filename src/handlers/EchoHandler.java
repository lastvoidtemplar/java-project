package handlers;

import command.CommandContext;
import command.CommandHandler;
import command.ResponseWriter;

public class EchoHandler implements CommandHandler {
    @Override
    public void handle(ResponseWriter writer, CommandContext ctx) {
        String echoResult = String.join(" ", ctx.cmd().commandArgs());
        writer.write(echoResult);
    }
}
