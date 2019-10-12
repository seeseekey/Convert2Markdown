package net.seeseekey.convert2markdown.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Html2Markdown {

    private static Logger logger = LoggerFactory.getLogger(new Exception().fillInStackTrace().getStackTrace()[0].getClassName());

    private static final String REGULAR_EXPRESSION_LINE_BREAK = "\r\n?|\n";

    private Html2Markdown() {}

    /**
     * Evaluate the nodes and extract the information for markdown (and convert too)
     *
     * @param node          Current node.
     * @param parentNode    Parent node.
     * @param stringBuilder StringBuilder for result
     */
    private static void evaluateNode(Node node, Node parentNode, StringBuilder stringBuilder) {

        if (node instanceof TextNode) {

            // Extract content from TextNode
            TextNode textNode = (TextNode) node;
            String wholeText = textNode.getWholeText();

            switch (parentNode.nodeName()) {
                case "a": {
                    wholeText = "[" + wholeText + "](" + parentNode.attr("href") + ")";
                    break;
                }
                case "blockquote":
                case "code":
                case "nowiki":
                case "pre": {
                    wholeText = "> " + wholeText.replace("\n", "\n> ");
                    break;
                }
                case "ul": {
                    wholeText = "";
                    break;
                }
            }

            stringBuilder.append(wholeText);
        } else {

            // Convert HTML tags into markdown
            switch (node.nodeName()) {
                case "a":
                case "blockquote":
                case "code":
                case "nowiki":
                case "pre": {

                    for (Node childNode : node.childNodes()) {
                        evaluateNode(childNode, node, stringBuilder);
                    }

                    break;
                }

                case "b": {

                    stringBuilder.append("**");

                    for (Node childNode : node.childNodes()) {
                        evaluateNode(childNode, node, stringBuilder);
                    }

                    stringBuilder.append("**");
                    break;
                }
                case "em": {

                    stringBuilder.append("*");

                    for (Node childNode : node.childNodes()) {
                        evaluateNode(childNode, node, stringBuilder);
                    }

                    stringBuilder.append("*");
                    break;
                }
                case "img": {
                    String markdownLink = "[" + node.attr("alt") + "](" + node.attr("src") + ")";
                    stringBuilder.append(markdownLink);
                    break;
                }
                case "li": {

                    stringBuilder.append("* ");

                    for (Node childNode : node.childNodes()) {
                        evaluateNode(childNode, node, stringBuilder);
                    }

                    stringBuilder.append("\n");
                    break;
                }
                case "ul": {

                    for (Node childNode : node.childNodes()) {
                        evaluateNode(childNode, node, stringBuilder);
                    }

                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                    break;
                }
                default: {
                    logger.info("Ignore unknown tag: {}", node.nodeName());

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
