package indexer.dataunit;

import java.util.HashMap;
import java.util.Vector;

public class ClassNode {

    private String name;//class name without .java suffix
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

    public ClassNode(String name, String url, String absolutePath) {
        this.name = name;
        this.url = url;
        this.absolutePath = absolutePath;

        this.methodTable = new HashMap<>();
        this.classLocation = null;
        this.definitionTable = new HashMap<>();
        this.importTable = new Vector<>();
        this.packageStr = "-";
    }

    public HashMap<String, Location> methodTable;
    public Location classLocation;
    public HashMap<String, Location> definitionTable;
    public Vector<String> importTable;

    public String getPackageStr() {
        return packageStr;
    }

    public void setPackageStr(String packageStr) {
        this.packageStr = packageStr;
    }

    private String packageStr;


    @Override
    public String toString() {
        return "ClassFile{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}