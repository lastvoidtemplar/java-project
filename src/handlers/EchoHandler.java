package handlers;

import command.CommandContext;
import command.CommandHandler;
import command.ResponseWriter;

public class EchoHandler implements CommandHandler {
    @Override
    public void handle(ResponseWriter writer, CommandContext ctx) {
//        try {
            String echoResult = ctx.cmd().commandName() + " " + String.join(" ", ctx.cmd().commandArgs());
//            Thread.sleep(1000);
            System.out.println(echoResult);
            writer.write(echoResult);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

    }
}
