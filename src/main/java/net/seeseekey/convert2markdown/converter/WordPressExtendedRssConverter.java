package net.seeseekey.convert2markdown.converter;

import com.rometools.rome.feed.module.DCModule;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import net.seeseekey.convert2markdown.options.FileScheme;
import net.seeseekey.convert2markdown.utils.DateTimeUtils;
import net.seeseekey.convert2markdown.utils.FileUtils;
import net.seeseekey.convert2markdown.utils.Html2Markdown;
import net.seeseekey.convert2markdown.utils.Logging;
import org.jdom2.Element;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class WordPressExtendedRssConverter implements Converter {

    private static final Logger LOG = Logging.getLogger();

    @Override
    public boolean canBeProcessed(String input) {

        try {
            String header = FileUtils.readFirstBytes(input, 3000);

            if (header.toLowerCase().contains("wordpress")) {
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
    public ConverterResult convert(String input, String filterByAuthor) throws IOException, FeedException {

        // Parse feed
        LOG.info("Loading feed...");
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
            Date publishedDate = entry.getPublishedDate();

            LocalDateTime localDateTime;

            if (publishedDate == null) {
                localDateTime = LocalDateTime.MIN;
            } else {
                localDateTime = DateTimeUtils.convertToLocalDateTime(publishedDate);
            }

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

            if(postTypeElement == null) {
                LOG.warn("Skip element, because postTypeElement is null.");
                continue;
            }

            String postType = postTypeElement.getValue();

            // Skip all non post and pages e.g. attachment
            switch (postType) {
                case "page" -> pages++;
                case "podcast", "post" -> posts++;
                default -> {
                    skipped++;
                    continue;
                }
            }

            // Get status
            Element statusElement = entry.getForeignMarkup().stream()
                    .filter(element -> "status".equals(element.getName()))
                    .findAny()
                    .orElse(null);

            if(statusElement == null) {
                LOG.warn("Skip element, because statusElement is null.");
                continue;
            }

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
            if (filterByAuthor != null && !filterByAuthor.equals(author)) {
                skipped++;
                continue;
            }

            // Get post id from foreign markup (via stream api)
            Element postIdElement = entry.getForeignMarkup().stream()
                    .filter(element -> "post_id".equals(element.getName()))
                    .findAny()
                    .orElse(null);

            if(postIdElement == null) {
                LOG.warn("Skip element, because postIdElement is null.");
                continue;
            }

            int postId = Integer.parseInt(postIdElement.getValue());

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("# ");
            stringBuilder.append(entry.getTitle().trim());
            stringBuilder.append("\n\n");

            // Content
            for (SyndContent content : entry.getContents()) {

                String contentAsString = Html2Markdown.convert(content.getValue());
                stringBuilder.append(contentAsString);
                stringBuilder.append("\n");
            }

            entries.add(new ConverterResultEntry(String.valueOf(postId), author, year, month, day, hour, minute, stringBuilder.toString()));
        }

        return new ConverterResult(entries, skipped, posts, pages);
    }
}
