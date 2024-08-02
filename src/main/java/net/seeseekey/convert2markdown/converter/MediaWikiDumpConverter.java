package net.seeseekey.convert2markdown.converter;

import com.rometools.rome.io.FeedException;
import net.seeseekey.convert2markdown.options.FileScheme;
import net.seeseekey.convert2markdown.utils.FileUtils;
import net.seeseekey.convert2markdown.utils.Logging;
import net.seeseekey.convert2markdown.utils.MediaWiki2Markdown;
import net.seeseekey.mediawikixml.wikipedia.WikiXMLParser;
import net.seeseekey.mediawikixml.wikipedia.WikiXMLParserFactory;
import org.slf4j.Logger;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MediaWikiDumpConverter implements Converter {

    private static final Logger LOG = Logging.getLogger();

    @Override
    public boolean canBeProcessed(String input) {

        try {
            String header = FileUtils.readFirstBytes(input, 3000);

            if (header.toLowerCase().contains("mediawiki")) {
                return true;
            }

        } catch (IOException e) {
            return false;
        }

        return false;
    }

    @Override
    public Set<FileScheme> getSupportedSchemes() {

        return Set.of(FileScheme.DATETIME, FileScheme.POST_ID);
    }

    @Override
    public ConverterResult convert(String input, String filterByAuthor) throws IOException, FeedException, XMLStreamException {

        WikiXMLParser wikiXMLParser = WikiXMLParserFactory.getParser(input);

        // Counter for statistic
        final int[] skipped = {0};
        int posts = 0;
        final int[] pages = {0};

        // List of entries
        List<ConverterResultEntry> entries = new ArrayList<>();

        try {

            wikiXMLParser.setPageCallback(page -> {

                String id = page.getId();
                String title = page.getTitle();

                ZonedDateTime localDateTime = page.getTimestamp();

                int year = localDateTime.getYear();
                int month = localDateTime.getMonth().getValue();
                int day = localDateTime.getDayOfMonth();

                int hour = localDateTime.getHour();
                int minute = localDateTime.getMinute();

                String content = page.getWikiText();

                // Skip empty pages
                if (content.trim().equals("")) {
                    skipped[0]++;
                    return;
                }

                content = "# " + title.trim() + "\n\n" + content.trim();

                content = MediaWiki2Markdown.convert(content);

                pages[0]++;
                entries.add(new ConverterResultEntry(id, "", year, month, day, hour, minute, content));
            });

            wikiXMLParser.parse();
        } catch (Exception e) {
            LOG.error("Error on reading page: ", e);
        }

        return new ConverterResult(entries, skipped[0], posts, pages[0]);
    }
}
