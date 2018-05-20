package indexer.dataunit.node;

import java.util.Vector;

public class DirNode implements Node {

    private String name;
    private String url;

    public DirNode(String name, String url) {
        this.name = name;
        this.url = url;
        child = new Vector<>();
    }

    private Vector<Node> child;

    public Vector<Node> getChild() {
        return child;
    }

    public void setChild(Vector<Node> child) {
        this.child = child;
    }

    public void add(Node node) {
        child.add(node);
        return;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Dir{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", child=" + child +
                '}';
    }
}
