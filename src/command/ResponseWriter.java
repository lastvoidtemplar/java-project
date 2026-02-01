package command;

import java.io.InputStream;

public interface ResponseWriter {
    void write(int len, InputStream inputStream);

    void write(String text);
}
