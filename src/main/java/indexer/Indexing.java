package indexer;

import indexer.project.Project;

public class Indexing {
    static public Project project = new Project();

    static public void main(String[] args) {
//        if (args.length == 0){
//            System.out.println("Please input a project path or a java class!");
//            return;
//        }
//        if (args.length >= 2){
//            System.out.println("Too many parameters!");
//            return;
//        }

        //initialization
//        project.setPath(args[0]);
        project.setPath("/Users/zhangxiaodong10/IdeaProjects/Symbol_Table");
        project.initialize();

        project.exploreWithVisitor(project.DEFINITION | project.REFERENCE,
                project.IMPORT | project.METHOD);
    }
}
