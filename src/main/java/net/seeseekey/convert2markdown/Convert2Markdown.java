package net.seeseekey.convert2markdown;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.CliFactory;
import com.rometools.rome.io.FeedException;
import net.seeseekey.convert2markdown.converter.Converter;
import net.seeseekey.convert2markdown.converter.ConverterResult;
import net.seeseekey.convert2markdown.converter.ConverterResultEntry;
import net.seeseekey.convert2markdown.converter.MediaWikiDumpConverter;
import net.seeseekey.convert2markdown.converter.WordPressExtendedRssConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Convert2Markdown {

    private static Logger logger = LoggerFactory.getLogger(new Exception().fillInStackTrace().getStackTrace()[0].getClassName());

    public static void main(String[] args) throws IOException, FeedException, XMLStreamException {

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
            logger.error("Input file must be set!");
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

        // Filter options
        String filterByAuthor = commandLineOptions.getAuthor();

        // Export options
        boolean exportAuthors = commandLineOptions.isAuthors();

        // Print message
        logger.info("Convert2Markdown");

        // Init converters
        MediaWikiDumpConverter mediaWikiDumpConverter = new MediaWikiDumpConverter();
        WordPressExtendedRssConverter wordPressExtendedRssConverter = new WordPressExtendedRssConverter();

        List<Converter> converters = new ArrayList<>();
        converters.add(mediaWikiDumpConverter);
        converters.add(wordPressExtendedRssConverter);

        // Init formatter
        DecimalFormat twoDigitsFormatter = new DecimalFormat("00", DecimalFormatSymbols.getInstance(Locale.ENGLISH));

        // Counter for statistic
        int skipped = 0;
        int posts = 0;
        int pages = 0;

        // Check format, process and write files
        for (Converter converter : converters) {

            if (converter.canProcessed(input)) {

                ConverterResult converterResult = converter.convert(input, filterByAuthor);

                skipped = converterResult.getSkipped();
                posts = converterResult.getPosts();
                pages = converterResult.getPages();

                for (ConverterResultEntry entry : converterResult.getEntries()) {

                    // Build path and create directories
                    String path = output + entry.getYear() + "/" + twoDigitsFormatter.format(entry.getMonth()) + "/";
                    File pathAsFile = new File(path);
                    pathAsFile.mkdirs();

                    // Get filename from scheme
                    String filename = null;

                    switch (scheme) {
                        case POST_ID: {
                            filename = path + entry.getId() + ".md";
                            break;
                        }
                        case DATETIME: {
                            filename = path
                                    + entry.getYear()
                                    + "-"
                                    + twoDigitsFormatter.format(entry.getMonth())
                                    + "-"
                                    + twoDigitsFormatter.format(entry.getDay())
                                    + "-"
                                    + twoDigitsFormatter.format(entry.getHour())
                                    + "-"
                                    + twoDigitsFormatter.format(entry.getMinute())
                                    + ".md";
                            break;
                        }
                    }

                    // Write markdown file
                    logger.info("Write file: " + filename);

                    try (PrintWriter fileWriter = new PrintWriter(filename)) {

                        // Heading (title)
                        String title = "# " + entry.getTitle().trim();
                        fileWriter.print(entry.getContent());

                        // Metadata
                        if (exportAuthors) {
                            fileWriter.println();
                            fileWriter.println("---");
                            fileWriter.println("Author: " + entry.getAuthor());
                        }
                    }
                }

                break;
            }
        }

        // Measure time
        double timeDifferenceInSeconds = (System.nanoTime() - startTime) / 1000000000.0; // Get seconds from nano seconds
        DecimalFormat decimalFormat = new DecimalFormat("#.00"); // Create pattern for formatting

        // Print out statistics
        logger.info("Skipped entries (e.g drafts, filtered entries, attachments): " + skipped);
        logger.info("Exported Pages: " + pages);
        logger.info("Exported Posts: " + posts);
        logger.info("Export completed in " + decimalFormat.format(timeDifferenceInSeconds) + " seconds");
    }
}
