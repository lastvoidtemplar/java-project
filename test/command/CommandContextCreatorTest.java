package command;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class CommandContextCreatorTest {
    @Test
    void testCommandContextCreatorTestWithInputWithoutQuotes() {
        String line = "echo one two three";
        CommandContext ctx = CommandContextCreator.newCommandContext(line, mock(), mock(), mock());
        Command cmd = ctx.cmd();

        assertEquals(
            "echo",
            cmd.commandName(),
            "Expected command name to be echo"
        );

        List<String> expectedArgs = List.of("one", "two", "three");
        List<String> actualArgs = cmd.commandArgs();
        assertEquals(
            expectedArgs.size(),
            actualArgs.size(),
            "the size of the actual arguments does not match the expected"
        );

        for (int i = 0; i < expectedArgs.size(); i++) {
            assertEquals(
                expectedArgs.get(i),
                actualArgs.get(i),
                "actual argument is different from the expected"
            );
        }
    }

    @Test
    void testCommandContextCreatorTestWithInputWithQuotes() {
        String line = "echo \"hello world\"";
        CommandContext ctx = CommandContextCreator.newCommandContext(line, mock(), mock(), mock());
        Command cmd = ctx.cmd();

        assertEquals(
            "echo",
            cmd.commandName(),
            "Expected command name to be echo"
        );

        List<String> expectedArgs = List.of("hello world");
        List<String> actualArgs = cmd.commandArgs();
        assertEquals(
            expectedArgs.size(),
            actualArgs.size(),
            "the size of the actual arguments does not match the expected"
        );

        for (int i = 0; i < expectedArgs.size(); i++) {
            assertEquals(
                expectedArgs.get(i),
                actualArgs.get(i),
                "actual argument is different from the expected"
            );
        }
    }
}
