package aog;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.text.TextContentRenderer;

public class MarkdownHTML {

    private final Parser parser;
    private final HtmlRenderer html_renderer;
    private final TextContentRenderer text_renderer;

    public MarkdownHTML() {
        parser = Parser.builder().build();
        html_renderer = HtmlRenderer.builder().escapeHtml(true).build();
        text_renderer = TextContentRenderer.builder().build();
    }

    public String markdownToHTML(String markdown) {
        Node node = parser.parse(markdown);
        return html_renderer.render(node);
    }

    public String markdownToText(String markdown) {
        Node node = parser.parse(markdown);
        return text_renderer.render(node);
    }

    public String markdownToText(String markdown, int limit_chars) {
        String raw_text = markdownToText(markdown);
        int num_chars = limit_chars > 3 ? limit_chars - 3 : 0;
        if (raw_text.length() <= limit_chars) {
            return raw_text;
        }

        StringBuilder text = new StringBuilder(raw_text.substring(0, num_chars));
        while (text.length() < limit_chars) {
            text.append(".");
        }
        return text.toString();
    }

}
