package indexer.query;

import indexer.dataunit.Location;
import indexer.dataunit.node.ClassNode;
import indexer.dataunit.node.DirNode;
import indexer.dataunit.node.Node;

import java.util.Vector;

public class Query {
    private String name;
    private String className;
    private Vector<String> imports;

    private Vector<Location> queryResult;

    public Query(){
        this.name = "";
        this.className = "";
        this.imports = new Vector<>();
    }
    public  void setQueryScope(String name, String declaringClassName, Vector<String> imports) {
        this.name = name;
        this.className = declaringClassName;
        this.imports = imports;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Vector<String> getImports() {
        return imports;
    }

    public void setImports(Vector<String> imports) {
        this.imports = imports;
    }

    public void customSearch(Node node) {

    }


    //without any scope restrict
    public void search(Node node) {
        if (node instanceof ClassNode) {

            if (((ClassNode) node).methodTable.containsKey(name)) {
                queryResult.add(((ClassNode) node).methodTable.get(name));
            }
        } else {
            for (Node subNode : ((DirNode) node).getChild()) {
                if (subNode instanceof ClassNode) {
                    if (((ClassNode) subNode).methodTable.containsKey(name)) {
                        queryResult.add(((ClassNode) subNode).methodTable.get(name));
                    }
                } else {
                    search(subNode);
                }
            }
        }
    }
}
