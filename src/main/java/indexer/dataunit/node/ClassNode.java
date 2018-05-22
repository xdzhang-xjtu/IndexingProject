package indexer.dataunit.node;

import indexer.dataunit.*;
import java.util.HashMap;
import java.util.Vector;

public class ClassNode  {

    private String name;
    private String url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String absolutePath;

    public String getAbsolutePath() {
        return absolutePath;
    }

    public ClassNode(String name, String absolutePath) {
        this.name = name;
//        this.url = url;
        this.absolutePath = absolutePath;

        this.methodTable = new HashMap<>();
        this.classTable = new HashMap<>();
        this.definitionTable = new HashMap<>();
        this.importTable = new Vector<>();
    }

    public HashMap<String, Location> methodTable;
    public HashMap<String, Location> classTable;
    public HashMap<String, Location> definitionTable;
    public Vector<String> importTable;


    @Override
    public String toString() {
        return "ClassFile{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}