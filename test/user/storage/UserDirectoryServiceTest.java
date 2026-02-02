package user.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class UserDirectoryServiceTest {
    private Path tempDir;

    @BeforeEach
    void setupUsersFile(@TempDir Path tempDir) throws IOException {
        this.tempDir = tempDir;
    }

    @Test
    void testUploadWithAlreadyExistingFile() throws IOException {
        Path user1Dir = tempDir.resolve("user1");
        Files.createDirectories(user1Dir);
        Path filePath = user1Dir.resolve("file.txt");
        Files.createFile(filePath);
        assertThrows(
            FileAlreadyExistsException.class,
            () -> new UserDirectoryService(tempDir).upload("user1", "file.txt", mock()),
            "Expected FileAlreadyExistsException when the file already exists"
        );
    }

    @Test
    void testUploadWithValidInput() throws FileAlreadyExistsException, IOException {
        Path user1Dir = tempDir.resolve("user1");
        Files.createDirectories(user1Dir);
        String fileContent = "hello world";

        InputStream stream = new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8));
        new UserDirectoryService(tempDir).upload("user1", "file.txt", stream);
        String actualFileContent = Files.readString(user1Dir.resolve("file.txt"));
        assertEquals(
            fileContent,
            actualFileContent,
            "incorrect upload"
        );
    }

    @Test
    void testDownloadWithMissingFile() throws IOException {
        Path user1Dir = tempDir.resolve("user1");
        Files.createDirectories(user1Dir);
        assertThrows(
            FileNotFoundException.class,
            () -> new UserDirectoryService(tempDir).download("user1", "file.txt"),
            "Expected FileNotFoundException when the file is not found"
        );
    }

    @Test
    void testDownloadWithValidInput() throws FileNotFoundException, IOException {
        Path user1Dir = tempDir.resolve("user1");
        Files.createDirectories(user1Dir);
        Path filePath = user1Dir.resolve("file.txt");
        Files.createFile(filePath);
        String fileContent = "hello world";
        Files.writeString(filePath, fileContent);

        DownloadResult result = new UserDirectoryService(tempDir).download("user1", "file.txt");
        try (var stream = result.fileStream()) {
            String actualFileContent = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            assertEquals(
                fileContent,
                actualFileContent,
                "incorrect download"
            );
        }
    }

    @Test
    void testListFiles() throws IOException {
        Path user1Dir = tempDir.resolve("user1");
        Files.createDirectories(user1Dir);
        Files.createFile(user1Dir.resolve("file1.txt"));
        Files.createFile(user1Dir.resolve("file2.txt"));

        Set<String> expectedFileNames = Set.of("file1.txt", "file2.txt");
        Set<String> actualFileNames = new HashSet<>(new UserDirectoryService(tempDir).list("user1"));

        assertEquals(
            expectedFileNames, actualFileNames,
            "difference between actual and expected filenames");
    }

    @Test
    void testDeleteFileWithMissingFile() throws IOException {
        Path user1Dir = tempDir.resolve("user1");
        Files.createDirectories(user1Dir);
        assertThrows(
            FileNotFoundException.class,
            () -> new UserDirectoryService(tempDir).delete("user1", "file.txt"),
            "Expected FileNotFoundException when the file is not found"
        );
    }

    @Test
    void testDeleteFileWithValidInput() throws FileNotFoundException, IOException {
        Path user1Dir = tempDir.resolve("user1");
        Files.createDirectories(user1Dir);
        Path filePath = user1Dir.resolve("file.txt");
        Files.createFile(filePath);
        new UserDirectoryService(tempDir).delete("user1", "file.txt");
        assertFalse(Files.exists(filePath), "incorrect deletion");
    }

    @Test
    void testLoadWithMissingFile() throws IOException {
        Path user1Dir = tempDir.resolve("user1");
        Files.createDirectories(user1Dir);
        assertThrows(
            FileNotFoundException.class,
            () -> new UserDirectoryService(tempDir).loadImage("user1", "image.jph"),
            "Expected FileNotFoundException when the image was not found"
        );
    }

    @Test
    void testLoadImageWithValidInput() throws FileNotFoundException, IOException {
        Path user1Dir = tempDir.resolve("user1");
        Files.createDirectories(user1Dir);
        BufferedImage image = new BufferedImage(10,10, BufferedImage.TYPE_INT_RGB);
        Path filePath = user1Dir.resolve("image.jpg");
        ImageIO.write(image, "jpg", filePath.toFile());
        BufferedImage loaded =  new UserDirectoryService(tempDir).loadImage("user1", "image.jpg");
        assertEquals(image.getWidth(), loaded.getWidth(), "incorrect image load");
        assertEquals(image.getHeight(), loaded.getHeight(), "incorrect image load");
    }

    @Test
    void testSaveImageWithoutAlreadyExisting() throws IOException{
        Path user1Dir = tempDir.resolve("user1");
        Files.createDirectories(user1Dir);
        BufferedImage image = new BufferedImage(10,10, BufferedImage.TYPE_INT_RGB);
        new UserDirectoryService(tempDir).saveImage("user1", "image.jpg", image);
        assertTrue(Files.exists(user1Dir.resolve("image.jpg")), "incorrect save");
    }

    @Test
    void testSaveImageWithAlreadyExisting() throws IOException{
        Path user1Dir = tempDir.resolve("user1");
        Files.createDirectories(user1Dir);
        Path filePath = user1Dir.resolve("image.jpg");
        Files.createFile(filePath);
        BufferedImage image = new BufferedImage(10,10, BufferedImage.TYPE_INT_RGB);
        new UserDirectoryService(tempDir).saveImage("user1", "image.jpg", image);
        assertTrue(Files.exists(filePath), "incorrect image save");
    }
}
