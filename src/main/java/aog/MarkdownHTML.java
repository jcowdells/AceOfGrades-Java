package aog;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class MarkdownHTML {

    private final Parser parser;
    private final HtmlRenderer renderer;

    public MarkdownHTML() {
        parser = Parser.builder().build();
        renderer = HtmlRenderer.builder().escapeHtml(true).build();
    }

    public String MarkdownToHTML(String markdown) {
        Node node = parser.parse(markdown);
        return renderer.render(node);
    }

}
