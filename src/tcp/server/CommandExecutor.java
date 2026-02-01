package tcp.server;

import auth.Session;
import command.CommandContext;
import command.CommandContextCreator;
import dispatcher.Dispatcher;
import dispatcher.MissingHandlerException;
import services.Services;

import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CommandExecutor {
    private final Selector selector;
    private final Services services;
    private final Dispatcher dispatcher;
    private final ConcurrentLinkedQueue<TcpResponseWriter> queue;

    public CommandExecutor(Selector selector, Services services, Dispatcher dispatcher) {
        this.selector = selector;
        this.services = services;
        this.dispatcher = dispatcher;
        this.queue = new ConcurrentLinkedQueue<>();
    }

    public void executeCommand(TcpResponseWriter writer, String input, Session session) {
        new Thread(() -> {
            CommandContext ctx = CommandContextCreator.newCommandContext(input, session, services);
            try {
                dispatcher.dispatch(writer, ctx);
            } catch (MissingHandlerException e) {
                writer.write("Unsupported command");
            }
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
