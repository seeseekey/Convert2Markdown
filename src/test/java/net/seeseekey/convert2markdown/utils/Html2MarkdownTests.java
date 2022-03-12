package net.seeseekey.convert2markdown.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Html2MarkdownTests {

    @Test
    void convertToMarkdownTest() {

        String html = TestUtils.getResourceAsString("testHTML.html");
        String markdown = Html2Markdown.convert(html);

        assertEquals(TestUtils.getResourceAsString("testHTML.md"), markdown);
    }
}
