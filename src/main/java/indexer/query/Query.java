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
        for (Map.Entry<String, ClassNode> classEntry : Indexing.project.projectRoot.entrySet()) {
            if (classEntry.getValue().getName().equals(declaringClassName)) {
                for (Map.Entry<String, Location> methodEntry : classEntry.getValue().methodTable.entrySet())
                    if (methodEntry.getKey().equals(methodName))
                        queryResult.add(methodEntry.getValue());
            }
        }
    }

    public void search() {
        if (Indexing.DEBUG) {
            if (declaringClassName == "" && absolutePath == "" && importsList.isEmpty())
                System.out.println("No restrict for the search scope!");
        }

        boolean classFlag = false;
        boolean packageFlag = false;
        boolean importFlag = false;

        //first, in one same class
        if (Indexing.project.projectRoot.containsKey(absolutePath)) {
            ClassNode classNode = Indexing.project.projectRoot.get(absolutePath);
            if (declaringClassName.equals(classNode.getName())) {
                //search in its method table
                if (classNode.methodTable.containsKey(methodName)) {
                    queryResult.add(classNode.methodTable.get(methodName));
                    // in fact, we should exit the method here. However, for validating we continue searching
                    classFlag = true;//do not continue the following search
                } else {
                    System.out.println("There is an exception the code! Let's Check it.....");
                    System.exit(0);
                }
            }// then goto a bigger scope
        } else {
            System.out.println("There are something wrong! Let's Check it.....");
            System.exit(0);
        }

        //second, in one same package
        if (classFlag == false) {
            for (Map.Entry<String, ClassNode> classEntry : Indexing.project.projectRoot.entrySet()) {
                ClassNode classNode = classEntry.getValue();
                if (selfPackage.equals(classNode.getPackageStr())) {//same package
                    if (declaringClassName.equals(classNode.getName()))//accuracy class
                        if (classNode.methodTable.containsKey(methodName)) {
                            queryResult.add(classNode.methodTable.get(methodName));
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
                if (entry.endsWith(declaringClassName) || entry.endsWith("*")) {
                    String packageName = entry.substring(0, entry.lastIndexOf("."));
                    if (Indexing.DEBUG) {
                        System.out.println(entry + " package name : " + packageName);
                    }
                    waitingList.add(packageName);
                }
            }

            for (String pEntry : waitingList) {
                for (Map.Entry<String, ClassNode> classEntry : Indexing.project.projectRoot.entrySet()) {
                    if (pEntry.equals(classEntry.getValue().getPackageStr())) {//same package
                        if (declaringClassName.equals(classEntry.getValue().getName()))//same class
                            if (classEntry.getValue().methodTable.containsKey(methodName)) {
                                //maybe contains multiple locations
                                queryResult.add(classEntry.getValue().methodTable.get(methodName));
                                importFlag = true;
                            }
                    }
                }
            }
        }

        //4th, in the total project
        if (classFlag == false && packageFlag == false && importFlag == false) {
            brutallySearch();
        }
    }
}
