package handlers;

import command.CommandContext;
import command.CommandHandler;
import command.ResponseWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class StreamHandler implements CommandHandler {
    @Override
    public void handle(ResponseWriter writer, CommandContext ctx) {
        String echoResult = String.join(" ", ctx.cmd().commandArgs());
        System.out.println(echoResult);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(ctx.stream()))) {
            reader.lines().forEach(System.out::println);
            System.out.println("finished");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
