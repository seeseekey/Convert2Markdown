package net.seeseekey.convert2markdown.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MediaWiki2MarkdownTests {

    @Test
    void convertToMarkdownTest() {

        String html = TestUtils.getResourceAsString("testMediaWiki.txt");
        String markdown = MediaWiki2Markdown.convert(html);

        assertEquals(TestUtils.getResourceAsString("testMediaWiki.md"), markdown);
    }
}
