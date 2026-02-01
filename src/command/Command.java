package command;

import java.util.List;

public record Command(String commandName, List<String> commandArgs) {
}
