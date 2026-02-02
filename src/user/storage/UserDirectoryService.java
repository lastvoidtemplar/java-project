package user.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class UserDirectoryService {
    private static final String FILE_ALREADY_EXISTS_MESSAGE = "File with this name already exists";
    private static final String FILE_NOT_FOUNT_MESSAGE = "File with this name was not found";

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
        Files.createDirectories(filePath.getParent());
        Files.copy(fileStream, filePath);
    }

    public DownloadResult download(String username, String filename) throws FileNotFoundException, IOException {
        Path filePath = root.resolve(username, filename);
        Files.createDirectories(filePath.getParent());
        if (!Files.exists(filePath)) {
            throw new FileNotFoundException(FILE_ALREADY_EXISTS_MESSAGE);
        }
        int fileSize = (int)Files.size(filePath);
        InputStream fileStream = Files.newInputStream(filePath);
        return new DownloadResult(fileSize, fileStream);
    }
}
