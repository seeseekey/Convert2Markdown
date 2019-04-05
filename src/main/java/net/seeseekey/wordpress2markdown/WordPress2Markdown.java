package net.seeseekey.wordpress2markdown;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.CliFactory;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.util.Locale;

public class WordPress2Markdown {

    private static Logger logger = LoggerFactory.getLogger(new Exception().fillInStackTrace().getStackTrace()[0].getClassName());

    public static void main(String[] args) throws IOException, FeedException {

        // Measure time
        long startTime = System.nanoTime();

        // Parse command line options
        final CommandLineOptions commandLineOptions;

        try {
            commandLineOptions = CliFactory.parseArguments(CommandLineOptions.class, args);
        } catch (ArgumentValidationException e) {
            System.out.println(e.getMessage());
            return;
        }

        String input = commandLineOptions.getInput();
        String output = commandLineOptions.getOutput();
        CommandLineOptions.Scheme scheme = commandLineOptions.getScheme();

        // Check given arguments
        if (input.isEmpty()) {
            logger.error("Input for WordPress eXtended RSS (WXR) must be set!");
            return;
        }

        if (!output.isEmpty()) {

            output = output.replace("\\", "/");

            if (output.charAt(output.length() - 1) != '/') {
                output += '/';
            }
        }

        if (scheme == null) {
            scheme = CommandLineOptions.Scheme.POST_ID;
        }

        // Print message
        logger.info("WordPress2Markdown");

        // Init formatter
        DecimalFormat twoDigitsFormatter = new DecimalFormat("00", DecimalFormatSymbols.getInstance(Locale.ENGLISH));

        // Parse feed
        logger.info("Loading feed...");
        URL feedUrl = new URL("file:" + input);
        SyndFeedInput syndFeedInput = new SyndFeedInput();
        SyndFeed syndFeed = syndFeedInput.build(new XmlReader(feedUrl));

        // Counter for statistic
        int posts = 0;
        int pages = 0;

        // Iterate all entries and export to markdown
        for (SyndEntry entry : syndFeed.getEntries()) {

            // Get published date to create path
            LocalDateTime localDateTime = Utils.convertToLocalDateTime(entry.getPublishedDate());

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
            switch(postType) {
                case "page": {
                    pages++;
                    break;
                }
                case "post": {
                    posts++;
                    break;
                }
                default: {
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
                continue;
            }

            // Get post id from foreign markup (via stream api)
            Element postIdElement = entry.getForeignMarkup().stream()
                    .filter(element -> "post_id".equals(element.getName()))
                    .findAny()
                    .orElse(null);

            int postId = Integer.parseInt(postIdElement.getValue());

            // Build path and create directories
            String path = output + year + "/" + twoDigitsFormatter.format(month) + "/";
            File pathAsFile = new File(path);
            pathAsFile.mkdirs();

            // Get filename from scheme
            String filename = null;

            switch (scheme) {
                case POST_ID: {
                    filename = path + postId + ".md";
                    break;
                }
                case DATETIME: {
                    filename = path
                            + year
                            + "-"
                            + twoDigitsFormatter.format(month)
                            + "-"
                            + twoDigitsFormatter.format(day)
                            + "-"
                            + twoDigitsFormatter.format(hour)
                            + "-"
                            + twoDigitsFormatter.format(minute)
                            + ".md";
                    break;
                }
            }

            // Write markdown file
            logger.info("Write file: " + filename);
            try (PrintWriter fileWriter = new PrintWriter(filename)) {


                String title = "# " + entry.getTitle().trim();
                fileWriter.println(title);
                fileWriter.println();

                for (SyndContent content : entry.getContents()) {

                    String contentAsString = Markdown.convert(content.getValue());
                    fileWriter.println(contentAsString);
                }
            }
        }

        // Measure time
        double timeDifferenceInSeconds = (System.nanoTime() - startTime) / 1000000000.0;
        DecimalFormat decimalFormat = new DecimalFormat("#.00");

        // Print out statistics
        logger.info("Pages: " + pages + " / Posts: " + posts);
        logger.info("Export completed in " +  decimalFormat.format(timeDifferenceInSeconds) + " seconds");
    }
}
