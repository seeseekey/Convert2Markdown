package net.seeseekey.convert2markdown.utils;

import org.slf4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * The FileUtils class provides utility methods for file operations.
 */
public class FileUtils {

    private static final Logger LOG = Logging.getLogger();

    private FileUtils() {
    }

    public static String readFirstBytes(String filename, int count) throws IOException {

        byte[] bytes = new byte[count];

        try (InputStream inputStream = new FileInputStream(filename)) {
            int read = inputStream.read(bytes);

            if(read < bytes.length) {
                LOG.info("Only read {} bytes of {} specified.", read, bytes.length);
            }
        }

        return new String(bytes, 0, bytes.length, Charset.defaultCharset()).trim();
    }
}
