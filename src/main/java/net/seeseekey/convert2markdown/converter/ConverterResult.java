package net.seeseekey.convert2markdown.converter;

import java.util.List;

public record ConverterResult(List<ConverterResultEntry> entries, int skipped, int posts, int pages) {

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
}
