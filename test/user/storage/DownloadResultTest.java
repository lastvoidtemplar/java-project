package user.storage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class DownloadResultTest {
    @Test
    void testDownloadResultTestWithNegativeSize() {
        assertThrows(
            IllegalArgumentException.class,
            ()->new DownloadResult(-1, mock()),
            "Expected IllegalArgumentException when stream size is negative"
        );
    }

    @Test
    void testDownloadResultTestWithNullStream() {
        assertThrows(
            IllegalArgumentException.class,
            ()->new DownloadResult(1024, null),
            "Expected IllegalArgumentException when stream is null"
        );
    }
}
