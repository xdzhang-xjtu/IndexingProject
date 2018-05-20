package indexer.dataunit.node;

import indexer.dataunit.table.Table;

import java.util.HashMap;

public class ClassNode implements Node {

    private String name;
    private String url;
    private String absolutePath;

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public ClassNode(String name, String url, String absolutePath) {
        this.name = name;
        this.url = url;
        this.absolutePath = absolutePath;
        this.methodTable = new HashMap<>();
        this.classTable = new HashMap<>();
        this.definitionTable = new HashMap<>();
    }

    public HashMap<String, String> methodTable;
    public HashMap<String, String> classTable;
    public HashMap<String, String> definitionTable;


    @Override
    public String toString() {
        return "ClassFile{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}