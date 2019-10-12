package net.seeseekey.convert2markdown.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MediaWiki2MarkdownTests {

    @Test
    void convertToMarkdownTest() {

        String html = TestUtils.getResourceAsString("testmediawiki.txt");
        String markdown = MediaWiki2Markdown.convert(html);

        assertEquals(TestUtils.getResourceAsString("testmediawiki.md"), markdown);
    }
}
