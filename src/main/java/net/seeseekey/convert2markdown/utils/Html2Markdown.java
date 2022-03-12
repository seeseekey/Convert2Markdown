package net.seeseekey.convert2markdown.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.slf4j.Logger;

public class Html2Markdown {

    private static final Logger log = Logging.getLogger();

    private static final String REGULAR_EXPRESSION_LINE_BREAK = "\r\n?|\n";

    private Html2Markdown() {
    }

    /**
     * Evaluate the nodes and extract the information for markdown (and convert too)
     *
     * @param node          Current node.
     * @param parentNode    Parent node.
     * @param stringBuilder StringBuilder for result
     */
    private static void evaluateNode(Node node, Node parentNode, StringBuilder stringBuilder) {

        if (node instanceof TextNode textNode) {

            // Extract content from TextNode
            String wholeText = textNode.getWholeText();

            switch (parentNode.nodeName()) {
                case "a" -> wholeText = "[" + wholeText + "](" + parentNode.attr("href") + ")";
                case "blockquote", "code", "nowiki", "pre" -> wholeText = "> " + wholeText.replace("\n", "\n> ");
                case "ul" -> wholeText = "";
                default -> {
                    // Ignore other nodes
                }
            }

            stringBuilder.append(wholeText);

        } else {

            // Convert HTML tags into markdown
            switch (node.nodeName()) {
                case "a", "blockquote", "code", "nowiki", "pre" -> {

                    for (Node childNode : node.childNodes()) {
                        evaluateNode(childNode, node, stringBuilder);
                    }
                }
                case "b", "strong" -> {

                    stringBuilder.append("**");

                    for (Node childNode : node.childNodes()) {
                        evaluateNode(childNode, node, stringBuilder);
                    }

                    stringBuilder.append("**");
                }
                case "em" -> {

                    stringBuilder.append("*");

                    for (Node childNode : node.childNodes()) {
                        evaluateNode(childNode, node, stringBuilder);
                    }

                    stringBuilder.append("*");
                }
                case "img" -> {
                    String markdownLink = "[" + node.attr("alt") + "](" + node.attr("src") + ")";
                    stringBuilder.append(markdownLink);
                }
                case "li" -> {

                    stringBuilder.append("* ");

                    for (Node childNode : node.childNodes()) {
                        evaluateNode(childNode, node, stringBuilder);
                    }

                    stringBuilder.append("\n");
                }
                case "ul" -> {

                    for (Node childNode : node.childNodes()) {
                        evaluateNode(childNode, node, stringBuilder);
                    }

                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                }
                default -> {
                    log.info("Ignore unknown tag: {}", node.nodeName());

                    for (Node childNode : node.childNodes()) {
                        evaluateNode(childNode, node, stringBuilder);
                    }
                }
            }
        }
    }

    /***
     * Converts a HTML string to markdown
     * @param html HTML string
     * @return A markdown string
     */
    public static String convert(String html) {

        // Replace all line endings with <br/>
        html = html.replace(REGULAR_EXPRESSION_LINE_BREAK, "<br/>");

        // Parse html document
        Document document = Jsoup.parse(html);

        // Create string builder for return value
        StringBuilder stringBuilder = new StringBuilder();

        // Iterate body and all children elements
        Element body = document.body();

        for (Node node : body.childNodes()) {
            evaluateNode(node, body, stringBuilder);
        }

        // Return markdown
        return stringBuilder.toString().trim();
    }
}
