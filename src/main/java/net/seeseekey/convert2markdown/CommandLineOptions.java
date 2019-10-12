package net.seeseekey.convert2markdown;

import com.lexicalscope.jewel.cli.Option;

public interface CommandLineOptions {

    @Option(description = "Input path", shortName = "i", defaultValue = "")
    String getInput();

    @Option(description = "Output path", shortName = "o", defaultValue = "")
    String getOutput();

    @Option(description = "Scheme of filenames", shortName = "s", defaultToNull = true, pattern = "POST_ID|DATETIME")
    Scheme getScheme();

    enum Scheme {
        POST_ID, DATETIME;
    }

    @Option(description = "Filter export by author", shortName = "f", defaultToNull = true)
    String getAuthor();

    @Option(description = "Export authors", shortName = "a")
    boolean isAuthors();

    @Option(description = "Show help", helpRequest = true, shortName = "h")
    boolean getHelp();
}