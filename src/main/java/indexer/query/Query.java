package indexer.query;

import indexer.Indexing;
import indexer.dataunit.Location;
import indexer.dataunit.ClassNode;

import java.util.Vector;

public class Query {
    private String token;// queried item
    private String selfPackage;
    private String methodDeclaringClass;
    private String innerClassDeclaringClass;
    private Vector<String> importsList;
    private String destPackage;
    private boolean queryInnerType;


    public Vector<Location> queryResult;

    public Query() {
        this.token = "-";
        this.selfPackage = "-";
        this.methodDeclaringClass = "-";
        this.innerClassDeclaringClass = "-";
        this.queryResult = new Vector<>();
        this.destPackage = "-";
    }

    public void setMethodQueryScope(String token, String destPackage, String methodDeclaringClass) {
        this.token = token;
        this.destPackage = destPackage;
        this.methodDeclaringClass = methodDeclaringClass;
    }

    public void setTypeQueryScope(String token, String destPackage, boolean isInnerClass, String innerClassDeclaringClass) {
        this.token = token;
        this.destPackage = destPackage;
        this.queryInnerType = isInnerClass;
        this.innerClassDeclaringClass = innerClassDeclaringClass;
    }


    public void searchMethod() {
        if (Indexing.project.projectData.containsKey(destPackage)) {
            Vector<ClassNode> classNodes = Indexing.project.projectData.get(destPackage);
            for (ClassNode classNode : classNodes) {
                if (methodDeclaringClass.equals(classNode.getClassName())) {
                    if (classNode.methodTable.containsKey(token)) {
                        Location loc = classNode.methodTable.get(token);
                        loc.scope = "DEST_PACKAGE";
                        queryResult.add(loc);
                    } else {
                        System.err.println("EXCEPTION 1: In the same package and same class, but there no the queried method");
                        System.exit(0);
                    }
                }
            }
            if (Indexing.DEBUG && queryResult.size() == 0) {
                System.err.println("ERROR 1: method" + token + " does not exist in package " + destPackage);
//                System.exit(0);
            }
        } else {
            System.err.println("ERROR 2: method" + destPackage + " does not exist in the source.");
            System.exit(0);
        }

        if (queryResult.size() == 0) {
            System.err.println("ERROR 3: Cannot find the method declaration.");
            System.exit(0);
        }
    }

    public void searchType() {//include class and interface
        if (Indexing.project.projectData.containsKey(destPackage)) {
            Vector<ClassNode> classNodes = Indexing.project.projectData.get(destPackage);
            if (queryInnerType == false) {
                for (ClassNode classNode : classNodes) {
                    if (token.equals(classNode.getClassName())) {
                        queryResult.add(classNode.classLocation);
                        break;
                    }
                }
            } else {
                for (ClassNode classNode : classNodes) {
                    if (innerClassDeclaringClass.equals(classNode.getClassName()) && classNode.hasInnerClass()) {
                        Location location = classNode.getInnerClassLocation(token);
                        if (location != null) {
                            queryResult.add(location);
                            break;
                        }
                    }
                }
            }
            if (Indexing.DEBUG && queryResult.size() == 0) {
                System.err.println("ERROR: Type " + token + " does not exist in package " + destPackage);
//                System.exit(0);
            }
        } else {
            System.err.println("ERROR: Type " + destPackage + " does not exist in the source.");
            System.exit(0);
        }
    }
}
