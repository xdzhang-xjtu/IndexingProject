package indexer;

import indexer.project.Project;
import indexer.dataunit.Statistics;

import java.io.File;

public class Indexing {
    final static public boolean DEBUG = true;
    final static public String[] externalLibs = {
            "java.applet",
            "java.awt",
            "java.beans",
            "java.io",
            "java.lang",
            "java.math",
            "java.net",
            "java.rmi",
            "java.security",
            "java.sql",
            "java.text",
            "java.util",
            "javax.accessibility",
            "javax.naming",
            "javax.rmi",
            "javax.sound",
            "javax.swing"};
    static public Project project = new Project();
    static public Statistics statistics = new Statistics();

    static public void main(String[] args) {
        statistics.setStartTimeTotal(System.currentTimeMillis());
        if (DEBUG) {
//            project.setPaths("/Users/zhangxiaodong10/IdeaProjects/javasyntax",
//                    "/Users/zhangxiaodong10/IdeaProjects/javasyntax/src/main/java");
//            project.setPaths("/Users/zhangxiaodong10/test/org.eclipse.jdt.apt.core",
//                    "/Users/zhangxiaodong10/test/org.eclipse.jdt.apt.core/src");
//            project.setPaths("/Users/xdzhang/IdeaProjects/IndexingProject",
//                    "/Users/xdzhang/IdeaProjects/IndexingProject/src/main/java");
            project.setPaths("/Users/zhangxiaodong10/IdeaProjects/IndexingProject",
                    "/Users/zhangxiaodong10/IdeaProjects/IndexingProject/src/main/java");

        } else {
            if (args.length < 2) {
                System.err.println("The args are not enough! Please input a java project and its source dir!");
                return;
            } else if (args.length > 2) {
                System.err.println("Too many parameters!");
                return;
            } else {
                File projectPath = new File(args[0]);
                if (!projectPath.exists()) {
                    System.err.println("The project path is wrong!");
                    return;
                }
                File source = new File(args[1]);
                if (!source.exists()) {
                    System.err.println("The source path is wrong!");
                    return;
                }

                project.setPaths(args[0], args[1]);
            }
        }

        //initialization
        project.initialize();
        //collecting info
        statistics.setStartTimeCollection(System.currentTimeMillis());
        project.applyDeclarationVisitor(project.IMPORT | project.PACKAGE | project.METHOD | project.TYPE);
        statistics.setEndTimeCollection(System.currentTimeMillis());
        if (!DEBUG) {
            project.test();
        }
        //indexing info
        statistics.setStartTimeIndexing(System.currentTimeMillis());
        project.applyReferenceVisitor(project.TYPE);
        statistics.setEndTimeIndexing(System.currentTimeMillis());

        statistics.setEndTimeTotal(System.currentTimeMillis());
        System.err.println(statistics);

    }
}
