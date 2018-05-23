package indexer;

import indexer.project.Project;

public class Indexing {
    final static public boolean DEBUG = true;
    static public Project project = new Project();

    static public void main(String[] args) {
        if (DEBUG) {
//            project.setPath("/Users/zhangxiaodong10/IdeaProjects/Symbol_Table");
            project.setPath("/Users/zhangxiaodong10/eclipse-workspace/Test");

        } else {
            if (args.length == 0) {
                System.out.println("Please input a project path or a java class!");
                return;
            }
            if (args.length >= 2) {
                System.out.println("Too many parameters!");
                return;
            }
            project.setPath(args[0]);
        }

        //initialization
        project.initialize();

        project.applyDeclarationVisitor(project.IMPORT | project.PACKAGE | project.METHOD);
        project.applyReferenceVisitor(project.METHOD);

        if (!DEBUG) {
            project.test();
        }
    }
}
