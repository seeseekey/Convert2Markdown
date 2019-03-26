package net.seeseekey.wordpress2markdown;

import com.lexicalscope.jewel.cli.Option;

interface CommandLineOptions {

    @Option(description = "input path", shortName = "i", defaultValue = "")
    String getInput();

    @Option(description = "output path", shortName = "o", defaultValue = "")
    String getOutput();

    @Option(description = "Scheme of filenames", shortName = "s", defaultToNull = true, pattern = "POST_ID|DATETIME")
    Scheme getScheme();

    enum Scheme {
        POST_ID, DATETIME;
    }

    @Option(helpRequest = true, shortName = "h")
    boolean getHelp();
}