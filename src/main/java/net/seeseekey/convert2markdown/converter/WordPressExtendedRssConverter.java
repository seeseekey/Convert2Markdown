package net.seeseekey.convert2markdown.converter;

import com.rometools.rome.feed.module.DCModule;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import net.seeseekey.convert2markdown.utils.FileUtils;
import net.seeseekey.convert2markdown.utils.Html2Markdown;
import net.seeseekey.convert2markdown.utils.DateTimeUtils;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class WordPressExtendedRssConverter implements Converter {

    private static Logger logger = LoggerFactory.getLogger(new Exception().fillInStackTrace().getStackTrace()[0].getClassName());

    @Override
    public boolean canProcessed(String input) {

        try {
            String header = FileUtils.readFirstBytes(input, 3000);

            if(header.toLowerCase().contains("wordpress")) {
                return true;
            }

        } catch (IOException e) {
            return false;
        }

        return false;
    }

    @Override
    public ConverterResult convert(String input, String filterByAuthor) throws IOException, FeedException {

        // Parse feed
        logger.info("Loading feed...");
        URL feedUrl = new URL("file:" + input);
        SyndFeedInput syndFeedInput = new SyndFeedInput();
        SyndFeed syndFeed = syndFeedInput.build(new XmlReader(feedUrl));

        // Counter for statistic
        int skipped = 0;
        int posts = 0;
        int pages = 0;

        // List of entries
        List<ConverterResultEntry> entries = new ArrayList<>();

        // Iterate all entries and export to markdown
        for (SyndEntry entry : syndFeed.getEntries()) {

            // Get published date to create path
            LocalDateTime localDateTime = DateTimeUtils.convertToLocalDateTime(entry.getPublishedDate());

            int year = localDateTime.getYear();
            int month = localDateTime.getMonth().getValue();
            int day = localDateTime.getDayOfMonth();

            int hour = localDateTime.getHour();
            int minute = localDateTime.getMinute();

            // Get postType
            Element postTypeElement = entry.getForeignMarkup().stream()
                    .filter(element -> "post_type".equals(element.getName()))
                    .findAny()
                    .orElse(null);

            String postType = postTypeElement.getValue();

            // Skip all non post and pages e.g. attachment
            switch (postType) {
                case "page": {
                    pages++;
                    break;
                }
                case "post": {
                    posts++;
                    break;
                }
                default: {
                    skipped++;
                    continue;
                }
            }

            // Get status
            Element statusElement = entry.getForeignMarkup().stream()
                    .filter(element -> "status".equals(element.getName()))
                    .findAny()
                    .orElse(null);

            String status = statusElement.getValue();

            // Only export published data
            if (!"publish".equals(status)) {
                skipped++;
                continue;
            }

            // Get author (via DC module)
            DCModule dcModule = (DCModule) entry.getModule("http://purl.org/dc/elements/1.1/");
            String author = dcModule.getCreator();

            // Check if author filter is activated and check if author passed the filter
            if (filterByAuthor != null) {
                if (!filterByAuthor.equals(author)) {
                    skipped++;
                    continue;
                }
            }

            // Get post id from foreign markup (via stream api)
            Element postIdElement = entry.getForeignMarkup().stream()
                    .filter(element -> "post_id".equals(element.getName()))
                    .findAny()
                    .orElse(null);

            int postId = Integer.parseInt(postIdElement.getValue());

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("# " + entry.getTitle().trim() + "\n");
            stringBuilder.append("\n");

            // Content
            for (SyndContent content : entry.getContents()) {

                String contentAsString = Html2Markdown.convert(content.getValue());
                stringBuilder.append(contentAsString + "\n");
            }

            entries.add(new ConverterResultEntry(String.valueOf(postId), author, year, month, day, hour, minute, entry.getTitle().trim(), stringBuilder.toString()));
        }

        return new ConverterResult(entries, skipped, posts, pages);
    }
}
