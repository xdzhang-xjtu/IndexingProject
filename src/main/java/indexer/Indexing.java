package indexer;

import indexer.dataunit.project.ProjectTree;

public class Indexing {
    static public ProjectTree project = new ProjectTree();

    static public void main(String[] args) {
//        if (args.length == 0){
//            System.out.println("Please input a project path or a java class!");
//            return;
//        }
//        if (args.length >= 2){
//            System.out.println("Too many parameters!");
//            return;
//        }

//        collectIndex(args[0]);
        //initialization
        project.setPath("/Users/zhangxiaodong10/IdeaProjects/Jump-in-a-file");
        project.initialize();

        //collect indexes in a total project
        project.collectIndex();

        //map indexes
    }
}
