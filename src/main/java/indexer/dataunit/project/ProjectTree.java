package indexer.dataunit.project;

import indexer.dataunit.node.*;

import java.io.File;

public class ProjectTree {
    private String path;
    /*
    if path == java file, projectRoot should be a File object; Otherwise a Dir object.
     */
    public Node projectRoot;

    public ProjectTree() {
    }


    public ProjectTree(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean initialize() {
        File file = new File(path);
        if (!file.exists()) {
            System.out.println("The project does not exist!");
            return false;
        }
        if (file.isDirectory())
            traverseFolder(new File(path), projectRoot);
        else {
            projectRoot = new ClassFile(file.getName(), path);
        }
        return true;
    }

    /*
    recursion
     */
    private void traverseFolder(File file, Node node) {

        if (file.exists()) {
            File[] files = file.listFiles();
            if (files.length == 0) {
                System.out.println(file.getName() + " is empty!");
            } else {
                node = new Dir(file.getName(), file.getAbsolutePath());
                for (File subfile : files) {
                    //filter out the .* dirs
                    if (subfile.getName().startsWith(".")) {
                        continue;
                    }
                    if (subfile.isDirectory()) {
                        Node subNode = new Dir(subfile.getName(), subfile.getAbsolutePath());
                        ((Dir) node).add(subNode);
                        System.out.println("Dir:" + subfile.getAbsolutePath());
                        traverseFolder(subfile, subNode);
                    } else {
                        if (subfile.getName().endsWith("java")) {
                            ((Dir) node).add(new ClassFile(subfile.getName(), subfile.getAbsolutePath()));
                            System.out.println("Java:" + subfile.getAbsolutePath() + ": " + subfile.getName());
                        }
                    }
                }
            }
        } else {
            System.out.println(file.getName() + " does not exist!");
        }
    }

}
