package net.seeseekey.convert2markdown;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.CliFactory;
import com.rometools.rome.io.FeedException;
import net.seeseekey.convert2markdown.converter.Converter;
import net.seeseekey.convert2markdown.converter.ConverterResult;
import net.seeseekey.convert2markdown.converter.ConverterResultEntry;
import net.seeseekey.convert2markdown.converter.CsvConverter;
import net.seeseekey.convert2markdown.converter.MediaWikiDumpConverter;
import net.seeseekey.convert2markdown.converter.WordPressExtendedRssConverter;
import net.seeseekey.convert2markdown.options.CommandLineOptions;
import net.seeseekey.convert2markdown.options.FileScheme;
import net.seeseekey.convert2markdown.utils.Logging;
import org.slf4j.Logger;

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

    private static final Logger LOG = Logging.getLogger();

    private static final String PATH_DELIMITER = "/";

    public static void main(String[] args) throws IOException, FeedException, XMLStreamException {

        // Measure time
        long startTime = System.nanoTime();

        // Parse command line options
        final CommandLineOptions commandLineOptions;

        try {
            commandLineOptions = CliFactory.parseArguments(CommandLineOptions.class, args);
        } catch (ArgumentValidationException e) {
            LOG.info(e.getMessage());
            return;
        }

        String input = commandLineOptions.getInput();
        String output = commandLineOptions.getOutput();
        FileScheme scheme = commandLineOptions.getScheme();

        // Check given arguments
        if (input.isEmpty()) {
            LOG.error("Input file must be set!");
            return;
        }

        if (!output.isEmpty()) {

            output = output.replace("\\", "/");

            if (output.charAt(output.length() - 1) != '/') {
                output += '/';
            }
        }

        if (scheme == null) {
            scheme = FileScheme.POST_ID;
        }

        // Filter options
        String filterByAuthor = commandLineOptions.getAuthor();

        // Export options
        boolean exportAuthors = commandLineOptions.isAuthors();

        // Print message
        LOG.info("Convert2Markdown");

        // Init converters
        CsvConverter csvConverter = new CsvConverter();
        MediaWikiDumpConverter mediaWikiDumpConverter = new MediaWikiDumpConverter();
        WordPressExtendedRssConverter wordPressExtendedRssConverter = new WordPressExtendedRssConverter();

        List<Converter> converters = new ArrayList<>();
        converters.add(csvConverter);
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

            if (converter.canBeProcessed(input)) {

                ConverterResult converterResult = converter.convert(input, filterByAuthor);

                skipped = converterResult.getSkipped();
                posts = converterResult.getPosts();
                pages = converterResult.getPages();

                for (ConverterResultEntry entry : converterResult.getEntries()) {

                    // Match scheme to supported scheme
                    if (!converter.getSupportedSchemes().contains(scheme)) {

                        scheme = converter.getSupportedSchemes().iterator().next();
                    }

                    String path = output;

                    if(scheme != FileScheme.SINGLE) {
                        // Build path and create directories
                        path = output + entry.getYear() + PATH_DELIMITER + twoDigitsFormatter.format(entry.getMonth()) + PATH_DELIMITER;
                        File pathAsFile = new File(path);

                        boolean created = pathAsFile.mkdirs();

                        if(!created) {
                            LOG.debug("Path {} exists already", path);
                        }
                    }

                    // Get filename from scheme
                    String filename = null;

                    switch (scheme) {
                        case DATETIME -> filename = path
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
                        case POST_ID -> filename = path + entry.getId() + ".md";
                        case SINGLE -> filename = path + "output.md";
                    }

                    // Write markdown file
                    LOG.info("Write file: {}", filename);

                    try (PrintWriter fileWriter = new PrintWriter(filename)) {

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
        LOG.info("Skipped entries (e.g drafts, filtered entries, attachments): {}", skipped);
        LOG.info("Exported Pages: {}", pages);
        LOG.info("Exported Posts: {}", posts);
        LOG.info("Export completed in {} seconds", decimalFormat.format(timeDifferenceInSeconds));
    }
}
