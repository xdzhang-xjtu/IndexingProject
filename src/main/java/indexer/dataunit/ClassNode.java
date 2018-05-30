package indexer.dataunit;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ClassNode {

    private String className;//class name without .java suffix
    private String packageName;
    private String absolutePath;
    public Location classLocation;

    public HashMap<String, Location> methodTable;// how to give a method a unique name
    public HashMap<String, Location> innerClassTable;//inner class name -- location
    public HashMap<String, Location> fieldTable;//inner class name -- location

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public ClassNode(String className, String packageName, String absolutePath) {
        this.className = className;
        this.packageName = packageName;
        this.absolutePath = absolutePath;
        this.classLocation = null;
        this.methodTable = new HashMap<>();
        this.innerClassTable = new HashMap<>();
        this.fieldTable = new HashMap<>();
    }

    public boolean hasInnerClass() {
        if (innerClassTable.size() == 0)
            return false;
        return true;
    }

    public boolean containtInnerClass(String className) {
        for (Map.Entry<String, Location> innerClassEntry : innerClassTable.entrySet()) {
            if (className.equals(innerClassEntry.getKey()))
                return true;
        }
        return false;
    }

    public Location getInnerClassLocation(String className) {
        if (innerClassTable.size() == 0) {
            System.err.println("This class has no inner class.");
            System.exit(0);
        }
        if (innerClassTable.containsKey(className)) {
            return innerClassTable.get(className);
        }
        return null;
    }

    @Override
    public String toString() {
        return "ClassFile{" +
                "class name='" + className + '\'' +
                ", package='" + packageName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassNode classNode = (ClassNode) o;
        return Objects.equals(className, classNode.className) &&
                Objects.equals(packageName, classNode.packageName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(className, packageName);
    }
}