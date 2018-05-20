package indexer.dataunit.node;

import java.util.Vector;

public class Dir implements Node {

    public String name;
    public String url;

    public Dir(String name, String url) {
        this.name = name;
        this.url = url;
        child = new Vector<>();
    }

    private Vector<Node> child;

    public void add(Node node) {
        child.add(node);
        return;
    }

}
