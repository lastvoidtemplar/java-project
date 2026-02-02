package user.storage;

import java.io.InputStream;

public record DownloadResult(int fileSize, InputStream fileStream) {
    private static final String FILE_SIZE_IS_NEGATIVE_MESSAGE = "File size must be non-negative";
    private static final String FILE_STREAM_NULL_MESSAGE = "File stream must be non-null";

    public DownloadResult {
        if (fileSize < 0) {
            throw new IllegalArgumentException(FILE_SIZE_IS_NEGATIVE_MESSAGE);
        }
        if (fileStream == null) {
            throw new IllegalArgumentException(FILE_STREAM_NULL_MESSAGE);
        }
    }
}
