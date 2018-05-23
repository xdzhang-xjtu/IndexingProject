package indexer.query;

import indexer.Indexing;
import indexer.dataunit.Location;
import indexer.dataunit.node.ClassNode;

import java.util.Map;
import java.util.Vector;

public class Query {
    private String methodName;
    private String selfPackage;
    private String declaringClassName;
    private String selfAbsolutePath;
    private Vector<String> importsList;
    private String destPackage;


    public Vector<Location> queryResult;

    public Query() {
        this.methodName = "-";
        this.selfPackage = "-";
        this.declaringClassName = "-";
        this.selfAbsolutePath = "-";
        this.queryResult = new Vector<>();
        this.destPackage = "-";
    }

    public void setQueryScope(String methodName, String selfPackage, String declaringClassName,
                              String selfAbsolutePath, Vector<String> importsList) {
        this.methodName = methodName;
        this.selfPackage = selfPackage;
        this.declaringClassName = declaringClassName;
        this.selfAbsolutePath = selfAbsolutePath;
        this.importsList = importsList;
    }

    public void setQueryScope(String methodName, String destPackage, String declaringClassName) {
        this.methodName = methodName;
        this.destPackage = destPackage;
        this.declaringClassName = declaringClassName;
    }

    public void brutallySearch() {
        for (Map.Entry<String, ClassNode> classEntry : Indexing.project.projectData.entrySet()) {
            if (declaringClassName.equals(classEntry.getValue().getName())) {
                if (!Indexing.DEBUG)
                    System.err.println(declaringClassName + "==" + classEntry.getValue().getName());
                if (!Indexing.DEBUG) {
                    System.err.println(declaringClassName + "=" + classEntry.getValue().getName());
                    for (Map.Entry<String, Location> methodEntry : classEntry.getValue().methodTable.entrySet())
                        System.err.println("Method: " + methodEntry.getKey());
                }
                if (classEntry.getValue().methodTable.containsKey(methodName)) {
                    Location location = classEntry.getValue().methodTable.get(methodName);
                    location.scope = "GLOBAL";
                    queryResult.add(location);
                }
            }
        }
    }

    public void search_v2() {
        boolean dest_package = false;
        for (Map.Entry<String, ClassNode> classEntry : Indexing.project.projectData.entrySet()) {
            ClassNode classNode = classEntry.getValue();
            if (destPackage.equals(classNode.getPackageStr()) &&
                    declaringClassName.equals(classNode.getName())) {
                if (classNode.methodTable.containsKey(methodName)) {
                    Location loc = classNode.methodTable.get(methodName);
                    loc.scope = "DEST_PACKAGE";
                    queryResult.add(loc);
                    dest_package = true;
                }else {
                    System.err.println("ERROR: There is no a method.");
                    System.exit(0);
                }
            }
        }

        //2nd, in the total project
        if (dest_package == false) {
            /*if the above three steps are sound,
            then we can conclude that this method is out of the project!
             */
            brutallySearch();
        }
    }

    public void search() {

        boolean classFlag = false;
        boolean packageFlag = false;
        boolean importFlag = false;

        //first, in one same class
        if (Indexing.project.projectData.containsKey(selfAbsolutePath)) {
            ClassNode classNode = Indexing.project.projectData.get(selfAbsolutePath);
            if (!Indexing.DEBUG)
                System.err.println("Query.serach() " + declaringClassName + " ? " + classNode.getName());
            if (declaringClassName.equals(classNode.getName())) {
                //search in its method table
                if (classNode.methodTable.containsKey(methodName)) {
                    Location loc = classNode.methodTable.get(methodName);
                    loc.scope = "CLASS";
                    queryResult.add(loc);
                    // in fact, we should exit the method here. However, for validating we continue searching
                    classFlag = true;//do not continue the following search
                } else {
                    System.err.println("ERROR: There is an exception the code! Let's Check it.....");
                    System.exit(0);
                }
            } else {
                if (!Indexing.DEBUG) {//classFlag = false
                    System.err.println("Goto a bigger scope from class to package");
                }
            }
        } else {
            System.err.println("ERROR: There are something wrong! Let's Check it.....");
            System.exit(0);
        }

        //second, in one same package
        if (classFlag == false) {
            for (Map.Entry<String, ClassNode> classEntry : Indexing.project.projectData.entrySet()) {
                ClassNode classNode = classEntry.getValue();
                if (selfPackage.equals(classNode.getPackageStr())) {//same package
                    if (declaringClassName.equals(classNode.getName()))//accuracy class
                        if (classNode.methodTable.containsKey(methodName)) {
                            Location loc = classNode.methodTable.get(methodName);
                            loc.scope = "PACKAGE";
                            queryResult.add(loc);
                            packageFlag = true;
                        }// otherwise, goto a bigger scope
                }
            }
        }

        //third, in the imports
        if (classFlag == false && packageFlag == false) {
            //determine the candidate packages
            Vector<String> waitingList = new Vector<>();
            for (String entry : importsList) {
                if (!Indexing.DEBUG) {
                    System.err.println("Declarating Class name: " + declaringClassName);
                }
                if (entry.endsWith("." + declaringClassName) || entry.endsWith(".*")) {
                    String packageName = entry.substring(0, entry.lastIndexOf("."));
                    if (!Indexing.DEBUG) {
                        System.err.println(entry + " package name : " + packageName);
                    }
                    waitingList.add(packageName);
                }
            }

            for (String pEntry : waitingList) {
                for (Map.Entry<String, ClassNode> classEntry : Indexing.project.projectData.entrySet()) {
                    if (pEntry.equals(classEntry.getValue().getPackageStr())) {//same package
                        if (declaringClassName.equals(classEntry.getValue().getName()))//same class
                            if (classEntry.getValue().methodTable.containsKey(methodName)) {
                                //maybe contains multiple locations
                                Location loc = classEntry.getValue().methodTable.get(methodName);
                                loc.scope = "IMPORTS";
                                queryResult.add(loc);
                                importFlag = true;
                            }
                    }
                }
            }
        }

        //4th, in the total project
        if (classFlag == false && packageFlag == false && importFlag == false) {
            /*if the above three steps are sound,
            then we can conclude that this method is out of the project!
             */
            brutallySearch();
        }
    }
}
