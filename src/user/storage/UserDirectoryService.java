package user.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class UserDirectoryService {
    private static final String FILE_ALREADY_EXISTS_MESSAGE = "File with this name already exists";

    private final Path root;

    public UserDirectoryService(Path root) {
        this.root = root;
    }

    public void upload(String username, String filename, InputStream fileStream)
        throws FileAlreadyExistsException, IOException {
        Path filePath = root.resolve(username, filename);
        if (Files.exists(filePath)) {
            throw new FileAlreadyExistsException(FILE_ALREADY_EXISTS_MESSAGE);
        }
//        Files.createDirectories(filePath.getParent());
        Files.copy(fileStream, filePath);
    }
}
