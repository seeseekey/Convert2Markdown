package net.seeseekey.convert2markdown.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class FileUtils {

    private FileUtils() {
    }

    public static String readFirstBytes(String filename, int count) throws IOException {

        byte[] bytes = new byte[count];

        try (InputStream inputStream = new FileInputStream(filename)) {
            inputStream.read(bytes);
        }

        return new String(bytes, 0, bytes.length, Charset.defaultCharset()).trim();
    }
}
