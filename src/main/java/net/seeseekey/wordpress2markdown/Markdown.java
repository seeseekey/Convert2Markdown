package net.seeseekey.wordpress2markdown;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Markdown {

    private static final Pattern patternBlockquote = Pattern.compile("<blockquote>(.+?)</blockquote>", Pattern.DOTALL);
    private static final Pattern patternPre = Pattern.compile("<pre>(.+?)</pre>", Pattern.DOTALL);

    private static final Pattern patternHyperlink = Pattern.compile("<\\s*a[^>]*>(.*?)<\\s*/\\s*a>", Pattern.DOTALL);
    private static final Pattern patternHyperlinkHref = Pattern.compile("href=([\"'])(.*?)\\1", Pattern.DOTALL);
    private static final Pattern patternHyperlinkLinkText = Pattern.compile(">.*</a>", Pattern.DOTALL);

    private static final Pattern patternCaptionBlock = Pattern.compile("\\[caption.*\\[/caption\\]", Pattern.DOTALL);

    private static final Pattern patternImgTag = Pattern.compile("<img.*?src=\"(.*?)\".*?(/>|</img>)", Pattern.DOTALL);
    private static final Pattern patternImgUrl = Pattern.compile("http(|s):.*\\.(jpg|png|jpeg|gif)", Pattern.DOTALL);
    private static final Pattern patternImgAlt = Pattern.compile("alt=\"(.*?)\"", Pattern.DOTALL);

    private static final Pattern patternLists = Pattern.compile("(?s)<ul>(.*?)</ul>", Pattern.DOTALL);

    private static final String REGULAR_EXPRESSION_LINE_BREAK = "(?s).*[\n\r].*";
    private static final String REGULAR_EXPRESSION_HTML_TAGS = "\\<.*?>";

    private static String convertBlockquotes(String html) {

        // Find all blockquote tags
        final Matcher matcher = patternBlockquote.matcher(html);
        StringBuffer stringBuffer = new StringBuffer(html.length());

        // Translate each tag
        while (matcher.find()) {

            // Get complete tag
            String group = matcher.group();

            // Remove tags
            group = group.replace("<blockquote>", "");
            group = group.replace("</blockquote>", "");

            // Replace line break with >
            group = group.replace("\n", "\n> ");
            group = "> " + group;

            // Escape regex replacement parameter ($) and others
            group = Matcher.quoteReplacement(group);

            // Replace old tag with markdown equivalent
            matcher.appendReplacement(stringBuffer, group);
        }

        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }

    private static String convertPres(String html) {

        // Find all pre tags
        final Matcher matcher = patternPre.matcher(html);
        StringBuffer stringBuffer = new StringBuffer(html.length());

        // Translate each tag
        while (matcher.find()) {

            // Get complete tag
            String group = matcher.group();

            // Remove tags
            group = group.replace("<pre>", "");
            group = group.replace("</pre>", "");

            // Replace line break with >
            group = group.replace("\n", "\n> ");
            group = "> " + group;

            // Escape regex replacement parameter ($) and others
            group = Matcher.quoteReplacement(group);

            // Replace old tag with markdown equivalent
            matcher.appendReplacement(stringBuffer, group);
        }

        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }

    private static String getHrefLink(String html) {

        final Matcher matcher = patternHyperlinkHref.matcher(html);

        if (matcher.find()) {
            return matcher.group().replace("\"", "").replace("href=", "");
        }

        return "";
    }

    private static String getHyperlinkText(String html) {

        final Matcher matcher = patternHyperlinkLinkText.matcher(html);

        if (matcher.find()) {
            return matcher.group().replace("</a>", "").replace(">", "");
        }

        return "";
    }

    private static String convertLinks(String html) {

        // Find all links
        final Matcher matcher = patternHyperlink.matcher(html);
        StringBuffer stringBuffer = new StringBuffer(html.length());

        // Translate each tag
        while (matcher.find()) {

            // Extract url and text from hyperlink
            String group = matcher.group();

            String hyperLinkURL = getHrefLink(group);
            String hyperLinkText = getHyperlinkText(group);

            // Build markdown link
            String markdownLink = "[" + hyperLinkText + "](" + hyperLinkURL + ")";

            // Escape regex replacement parameter ($) and others
            markdownLink = Matcher.quoteReplacement(markdownLink);

            // Replace old tag with markdown equivalent
            matcher.appendReplacement(stringBuffer, markdownLink);
        }

        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }

    private static String getImgTag(String html) {

        final Matcher matcher = patternImgTag.matcher(html);

        if (matcher.find()) {
            return matcher.group();
        }

        return "";
    }

    private static String getImgUrl(String html) {

        final Matcher matcher = patternImgUrl.matcher(html);

        if (matcher.find()) {
            return matcher.group();
        }

        return "";
    }

    private static String getImgAlt(String html) {

        final Matcher matcher = patternImgAlt.matcher(html);

        if (matcher.find()) {
            String group = matcher.group().replace("alt=\"", "");
            return group.substring(0, group.length() - 1);
        }

        return "";
    }

    private static String convertCaptionBlocks(String html) {

        // Find all caption blocks
        final Matcher matcher = patternCaptionBlock.matcher(html);
        StringBuffer stringBuffer = new StringBuffer(html.length());

        // Translate each tag
        while (matcher.find()) {

            // Extract url and text from hyperlink
            String group = matcher.group();

            String imgTag = getImgTag(group);
            String imageUrl = getImgUrl(imgTag);

            String captionText = group.replaceAll(REGULAR_EXPRESSION_HTML_TAGS, "")
                    .replaceAll("\\[caption.*?\\]", "")
                    .replaceAll("\\[/caption\\]", "")
                    .trim();

            // Build markdown link
            String markdownLink = "[" + captionText + "](" + imageUrl + ")";

            // Escape regex replacement parameter ($) and others
            markdownLink = Matcher.quoteReplacement(markdownLink);

            // Replace old tag with markdown equivalent
            matcher.appendReplacement(stringBuffer, markdownLink);
        }

        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }

    private static String convertImages(String html) {

        // Find all img tags
        final Matcher matcher = patternImgTag.matcher(html);
        StringBuffer stringBuffer = new StringBuffer(html.length());

        // Translate each tag
        while (matcher.find()) {

            // Extract url and text from hyperlink
            String group = matcher.group();

            String imageUrl = getImgUrl(group);
            String imageAltText = getImgAlt(group);

            // Build markdown link
            String markdownLink = "[" + imageAltText + "](" + imageUrl + ")";

            // Escape regex replacement parameter ($) and others
            markdownLink = Matcher.quoteReplacement(markdownLink);

            // Replace old tag with markdown equivalent
            matcher.appendReplacement(stringBuffer, markdownLink);
        }

        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }

    private static String convertLists(String html) {

        // Find all lists
        final Matcher matcher = patternLists.matcher(html);
        StringBuffer stringBuffer = new StringBuffer(html.length());

        // Translate each tag
        while (matcher.find()) {

            // Extract url and text from hyperlink
            String group = matcher.group();
            group = group.replaceAll(REGULAR_EXPRESSION_HTML_TAGS, ""); // Strip html tags

            String[] lines = group.split("\n");
            group = "";

            for(String line : lines) {

                if(line.trim().isEmpty()) {
                    continue;
                }

                group += "* " + line.trim() + "\n";
            }

            // Escape regex replacement parameter ($) and others
            group = Matcher.quoteReplacement(group);

            // Replace old tag with markdown equivalent
            matcher.appendReplacement(stringBuffer, group.substring(0, group.length()-1));
        }

        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }

    /***
     * Converts a HTML string to markdown
     * @param html HTML string
     * @return A markdown string
     */
    public static String convert(String html) {

        // Trim whitespaces
        String markdown = html.trim();

        // Replace all line endings
        markdown = markdown.replace(REGULAR_EXPRESSION_LINE_BREAK, "\n");

        // Convert <em>
        markdown = markdown.replace("<em>", "*");
        markdown = markdown.replace("</em>", "*");

        // Convert <b>
        markdown = markdown.replace("<b>", "**");
        markdown = markdown.replace("</b>", "**");

        // Convert <blockquote>
        markdown = convertBlockquotes(markdown);

        // Convert <pre>
        markdown = convertPres(markdown);

        // Convert residual <img> tags
        markdown = convertImages(markdown);

        // Convert <a>
        markdown = convertLinks(markdown);

        // Convert [caption] blocks
        markdown = convertCaptionBlocks(markdown);

        // Convert lists <ul> <li>
        markdown = convertLists(markdown);

        // Strip residual html tags
        markdown = markdown.replaceAll(REGULAR_EXPRESSION_HTML_TAGS, "");

        // Return markdown
        return markdown;
    }
}
