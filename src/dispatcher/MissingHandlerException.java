package dispatcher;

public class MissingHandlerException extends Exception {
    public MissingHandlerException(String message) {
        super(message);
    }

    public MissingHandlerException(String message, Throwable cause) {
        super(message, cause);
    }
}
