package net.seeseekey.convert2markdown.converter;

import java.util.List;

public class ConverterResult {

    private int skipped = 0;
    private int posts = 0;
    private int pages = 0;

    private List<ConverterResultEntry> entries;

    public List<ConverterResultEntry> getEntries() {
        return entries;
    }

    public int getSkipped() {
        return skipped;
    }

    public int getPosts() {
        return posts;
    }

    public int getPages() {
        return pages;
    }

    public ConverterResult(List<ConverterResultEntry> entries, int skipped, int posts, int pages) {

        this.entries = entries;

        this.skipped = skipped;
        this.posts = posts;
        this.pages = pages;
    }
}
