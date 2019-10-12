package net.seeseekey.convert2markdown.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MediaWiki2Markdown {

    private MediaWiki2Markdown() {
    }

    private static final Pattern patternHeading2 = Pattern.compile("==(.+?)==", Pattern.DOTALL);
    private static final Pattern patternHeading3 = Pattern.compile("===(.+?)===", Pattern.DOTALL);
    private static final Pattern patternHeading4 = Pattern.compile("====(.+?)====", Pattern.DOTALL);
    private static final Pattern patternHeading5 = Pattern.compile("=====(.+?)=====", Pattern.DOTALL);
    private static final Pattern patternHeading6 = Pattern.compile("======(.+?)======", Pattern.DOTALL);

    private static String convertHeading(String mediawiki, Pattern patternHeading, int level) {

        // Find all blockquote tags
        final Matcher matcher = patternHeading.matcher(mediawiki);
        StringBuffer stringBuffer = new StringBuffer(mediawiki.length());

        // Translate each tag
        while (matcher.find()) {

            // Get complete tag
            String group = matcher.group();

            // Remove tags
            String heading = "";

            for(int i=0; i<level; i++) {
                heading += "=";
            }

            String headingReplacement = "";

            for(int i=0; i<level; i++) {
                headingReplacement += "#";
            }

            headingReplacement += " ";

            group = group.replaceFirst(heading, headingReplacement);
            group = group.replace(heading, "");

            // Escape regex replacement parameter ($) and others
            group = Matcher.quoteReplacement(group);

            // Replace old tag with markdown equivalent
            matcher.appendReplacement(stringBuffer, group);
        }

        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }

    public static String convert(String mediawiki) {

        // First convert html tags
        mediawiki = Html2Markdown.convert(mediawiki);

        // Remove inter wiki links
        mediawiki = mediawiki.replace("[[", "");
        mediawiki = mediawiki.replace("]]", "");

        // Convert bold and italic
        mediawiki = mediawiki.replace("'''", "**");
        mediawiki = mediawiki.replace("''", "*");

        // Process headings
        mediawiki = convertHeading(mediawiki, patternHeading6, 6);
        mediawiki = convertHeading(mediawiki, patternHeading5, 5);
        mediawiki = convertHeading(mediawiki, patternHeading4, 4);
        mediawiki = convertHeading(mediawiki, patternHeading3, 3);
        mediawiki = convertHeading(mediawiki, patternHeading2, 2);

        // Return markdown
        return mediawiki;
    }
}
