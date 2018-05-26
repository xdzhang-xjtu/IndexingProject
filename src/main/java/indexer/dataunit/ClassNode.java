package indexer.dataunit;

import indexer.Indexing;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class ClassNode {

    private String name;//class name without .java suffix
    private String url;
    public boolean hasInnerClass;

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
        this.hasInnerClass = false;
        this.methodTable = new HashMap<>();
        this.classLocation = null;
        this.definitionTable = new HashMap<>();
        this.importTable = new Vector<>();
        this.innerClassTable = new HashMap<>();
        this.packageStr = "-";
    }

    public HashMap<String, Location> methodTable;
    public HashMap<String, Location> innerClassTable;//inner class name -- location
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

    public boolean containtInnerClass(String className) {
        for (Map.Entry<String, Location> innerClassEntry : innerClassTable.entrySet()){
            if (className.equals(innerClassEntry.getKey()))
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "ClassFile{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}