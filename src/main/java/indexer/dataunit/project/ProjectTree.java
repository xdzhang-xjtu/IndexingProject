package indexer.dataunit.project;

import indexer.dataunit.node.*;
import indexer.visitor.definition.*;

import java.io.File;
import java.util.Vector;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;

public class ProjectTree {
    private String path;
    private Vector<ASTVisitor> astVisitors;
    /*
    if path == java file, projectRoot should be a File object; Otherwise a Dir object.
     */
    public Node projectRoot;


    public ProjectTree() {
        astVisitors = new Vector<>();
    }


    public ProjectTree(String path) {
        this.path = path;
        astVisitors = new Vector<>();
    }

    public void collectIndex() {
        exploreByMethodDefVisitor(projectRoot);
//        exploreByClassDefVisitor(projectRoot);
//        exploreByVariableDefVisitor(projectRoot);
    }


    public void exploreByMethodDefVisitor(Node node) {
        if (node instanceof ClassNode) {
            CompilationUnit compilationUnit = buildCompilationUnit(((ClassNode) node).getAbsolutePath());
            MethodDefinitionVisitor astVisitor = new MethodDefinitionVisitor(compilationUnit, (ClassNode) node);
            compilationUnit.accept(astVisitor);
        } else {
            for (Node subNode : ((DirNode) node).getChild()) {
                if (subNode instanceof ClassNode) {
                    CompilationUnit compilationUnit = buildCompilationUnit(((ClassNode) subNode).getAbsolutePath());
                    MethodDefinitionVisitor astVisitor = new MethodDefinitionVisitor(compilationUnit, (ClassNode) subNode);
                    compilationUnit.accept(astVisitor);
                } else {
                    exploreByMethodDefVisitor(subNode);
                }
            }
        }
    }

    public void exploreByClassDefVisitor(Node node) {
        if (node instanceof ClassNode) {
            CompilationUnit compilationUnit = buildCompilationUnit(((ClassNode) node).getAbsolutePath());
            ClassDefinitionVisitor astVisitor = new ClassDefinitionVisitor(compilationUnit, (ClassNode) node);
            compilationUnit.accept(astVisitor);
        } else {
            for (Node subNode : ((DirNode) node).getChild()) {
                if (subNode instanceof ClassNode) {
                    CompilationUnit compilationUnit = buildCompilationUnit(((ClassNode) subNode).getAbsolutePath());
                    ClassDefinitionVisitor astVisitor = new ClassDefinitionVisitor(compilationUnit, (ClassNode) subNode);
                    compilationUnit.accept(astVisitor);
                } else {
                    exploreByClassDefVisitor(subNode);
                }
            }
        }
    }

    public void exploreByVariableDefVisitor(Node node) {
        if (node instanceof ClassNode) {
            CompilationUnit compilationUnit = buildCompilationUnit(((ClassNode) node).getAbsolutePath());
            VariableDefinitionVisitor astVisitor = new VariableDefinitionVisitor(compilationUnit, (ClassNode) node);
            compilationUnit.accept(astVisitor);
        } else {
            for (Node subNode : ((DirNode) node).getChild()) {
                if (subNode instanceof ClassNode) {
                    CompilationUnit compilationUnit = buildCompilationUnit(((ClassNode) subNode).getAbsolutePath());
                    VariableDefinitionVisitor astVisitor = new VariableDefinitionVisitor(compilationUnit, (ClassNode) subNode);
                    compilationUnit.accept(astVisitor);
                } else {
                    exploreByVariableDefVisitor(subNode);
                }
            }
        }
    }


    public CompilationUnit buildCompilationUnit(String absolutePath) {
        File file = new File(absolutePath);
        String str = "";
        try {
            str = FileUtils.readFileToString(file, "UTF-8");
        } catch (IOException e) {
            System.out.println(e);
        }

        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setResolveBindings(true);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setBindingsRecovery(true);
        Map options = JavaCore.getOptions();
        parser.setCompilerOptions(options);
        parser.setUnitName(file.getName());
//        String[] sources = {"/Users/zhangxiaodong10/IdeaProjects/indexer/src/"};
//        String[] classpath = {"/Library/Java/JavaVirtualMachines/jdk1.8.0_172.jdk/Contents/Home/jre/librt.jar"};
//        parser.setEnvironment(classpath, sources, new String[]{"UTF-8"}, true);
        parser.setSource(str.toCharArray());
        return (CompilationUnit) parser.createAST(null);
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
        if (file.isDirectory()) {
            projectRoot = new DirNode(file.getName(), file.getName());
            traverseFolder(new File(path), projectRoot);
        } else {
            projectRoot = new ClassNode(file.getName(), "", path);
        }
        return true;
    }

    /*
    recursion
     */
    private void traverseFolder(File file, Node node) {
//        System.out.println(node);

        if (file.exists()) {
            File[] files = file.listFiles();
            if (files.length == 0) {
                System.out.println(file.getName() + " is empty!");
            } else {
                for (File subfile : files) {
                    if (subfile.getName().startsWith(".")) {//filter out the .* dirs
                        continue;
                    }
                    if (subfile.isDirectory()) {
                        Node subNode = new DirNode(subfile.getName(), ((DirNode) node).getUrl() + "." + subfile.getName());
                        ((DirNode) node).add(subNode);
                        traverseFolder(subfile, subNode);
                    } else {
                        if (subfile.getName().endsWith("java")) {
                            Node classFile = new ClassNode(subfile.getName(), ((DirNode) node).getUrl(),
                                    subfile.getAbsolutePath());
                            ((DirNode) node).add(classFile);
//                            System.out.println(classFile);
                        }
                    }
                }
            }
        } else {
            System.out.println(file.getName() + " does not exist!");
        }
    }

}
