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
    private String absolutePath;
    private Vector<String> importsList;


    public Vector<Location> queryResult;

    public Query() {
        this.methodName = "";
        this.selfPackage = "";
        this.declaringClassName = "";
        this.absolutePath = "";
        this.queryResult = new Vector<>();
    }

    public void setQueryScope(String methodName, String selfPackage, String declaringClassName, String absolutePath, Vector<String> importsList) {
        this.methodName = methodName;
        this.selfPackage = selfPackage;
        this.declaringClassName = declaringClassName;
        this.absolutePath = absolutePath;
        this.importsList = importsList;
    }

    public void brutallySearch() {
        for (Map.Entry<String, ClassNode> classEntry : Indexing.project.projectData.entrySet()) {
            if (declaringClassName.equals(classEntry.getValue().getName())) {
                if (Indexing.DEBUG)
                    System.err.println(declaringClassName + "==" + classEntry.getValue().getName());
                if (!Indexing.DEBUG) {
                    System.err.println(declaringClassName + "=" + classEntry.getValue().getName());
                    for (Map.Entry<String, Location> methodEntry : classEntry.getValue().methodTable.entrySet())
                        System.err.println("Method: " + methodEntry.getKey());
                }
                if (classEntry.getValue().methodTable.containsKey(methodName)) {
                    System.err.println("Method: " + methodName);

                    Location location = classEntry.getValue().methodTable.get(methodName);
                    location.scope = "GLOBAL";
                    queryResult.add(location);

                    System.err.println(location);
//                    System.exit(0);
                }
            }
        }
    }

    public void search() {
        if (Indexing.DEBUG) {
            if (declaringClassName == "" && absolutePath == "" && importsList.isEmpty())
                System.err.println("No restrict for the search scope!");
        }

        boolean classFlag = false;
        boolean packageFlag = false;
        boolean importFlag = false;

        //first, in one same class
        if (Indexing.project.projectData.containsKey(absolutePath)) {
            ClassNode classNode = Indexing.project.projectData.get(absolutePath);
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
                System.err.println("Import name " + entry);
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


                if (entry.endsWith(".*")) {
                    String packageName = entry.substring(0, entry.lastIndexOf("."));
                    System.err.println("Import" + entry + " package name : " + packageName);
                    System.exit(0);
                }
            }

            for (String pEntry : waitingList) {
                for (Map.Entry<String, ClassNode> classEntry : Indexing.project.projectData.entrySet()) {
                    if (pEntry.equals(classEntry.getValue().getPackageStr())) {//same package
                        if (declaringClassName.equals(classEntry.getValue().getName()))//same class
                            if (classEntry.getValue().methodTable.containsKey(methodName)) {
                                //maybe contains multiple locations
                                Location loc=classEntry.getValue().methodTable.get(methodName);
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
