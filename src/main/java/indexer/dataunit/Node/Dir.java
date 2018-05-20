package indexer.dataunit.Node;

import java.util.Vector;

public class Dir extends Node {
    public Dir(String name) {
        super(name);
    }

    Vector<Node> child;
}
