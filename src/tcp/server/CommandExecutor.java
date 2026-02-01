package tcp.server;

import command.CommandContext;
import command.CommandContextCreator;
import handlers.EchoHandler;

import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CommandExecutor {
    private final Selector selector;
    private final ConcurrentLinkedQueue<TcpResponseWriter> queue;

    public CommandExecutor(Selector selector) {
        this.selector = selector;
        this.queue = new ConcurrentLinkedQueue<>();
    }

    public void executeCommand(TcpResponseWriter writer, String input) {
        new Thread(() -> {
            CommandContext ctx = CommandContextCreator.newCommandContext(input);
            new EchoHandler().handle(writer, ctx);
            queue.add(writer);
            selector.wakeup();
        }).start();
    }

    public List<TcpResponseWriter> drainReadyResponseWriters() {
        List<TcpResponseWriter> drained = new ArrayList<>();
        TcpResponseWriter elem;
        while ((elem = queue.poll()) != null) {
            drained.add(elem);
        }
        return drained;
    }
}
