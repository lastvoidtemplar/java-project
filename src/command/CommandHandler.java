package command;

public interface CommandHandler {
    void handle(ResponseWriter writer, CommandContext ctx);
}
