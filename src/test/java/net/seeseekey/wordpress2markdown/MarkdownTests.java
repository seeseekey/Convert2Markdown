package net.seeseekey.wordpress2markdown;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MarkdownTests {

    public static String getResourceAsString(String resource) {

        URL url = Resources.getResource(resource);
        try {
            return Resources.toString(url, Charsets.UTF_8);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Test
    void convertToMarkdownTest() {

        String html = getResourceAsString("test.html");
        String markdown = Markdown.convert(html);

        assertEquals(getResourceAsString("test.md"), markdown);
    }
}
