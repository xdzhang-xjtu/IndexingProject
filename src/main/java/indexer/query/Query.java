package indexer.query;

import indexer.Indexing;
import indexer.dataunit.Location;
import indexer.dataunit.node.ClassNode;
import indexer.dataunit.node.DirNode;
import indexer.dataunit.node.Node;

import java.util.Map;
import java.util.Vector;

public class Query {
    private String methodName;
    private String declaringClassName;
    private String absolutePath;
    private Vector<String> importsList;


    public Vector<Location> queryResult;

    public Query() {
        this.methodName = "";
        this.declaringClassName = "";
        this.absolutePath = "";
        this.importsList = new Vector<>();
        this.queryResult = new Vector<>();
    }

    public void setQueryScope(String methodName, String declaringClassName, String absolutePath, Vector<String> importsList) {
        this.methodName = methodName;
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
        if (declaringClassName == "" && absolutePath == "" && importsList.isEmpty())
            System.out.println("No restrict for the search scope!");

        //firstly, in the same file and same class
        if (Indexing.project.projectRoot.containsKey(absolutePath)) {
            ClassNode classNode = Indexing.project.projectRoot.get(absolutePath);
            if (declaringClassName.equals(classNode.getName())) {
                //search in its method table
                if (classNode.methodTable.containsKey(methodName)) {
                    queryResult.add(classNode.methodTable.get(methodName));
                    // in fact, we should exit the method here. However, for validating we continue searching
                    return;
                } else {
                    System.out.println("There is an exception the code! Let's Check it.....");
                    System.exit(0);
                }
            }// then goto a bigger scope
        } else {
            System.out.println("There are something wrong! Let's Check it.....");
            System.exit(0);
        }

        //secondly,
        for (Map.Entry<String, ClassNode> classEntry : Indexing.project.projectRoot.entrySet()) {
            System.out.println("#Class File " + classEntry.getKey() + "has Method Declarations: ");
            ClassNode classNode = classEntry.getValue();
            for (Map.Entry<String, Location> methodEntry : classNode.methodTable.entrySet())
                System.out.println(methodEntry.getKey() + " at Line" + methodEntry.getValue().lineNumber);
        }
    }
}
