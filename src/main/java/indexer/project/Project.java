package indexer.project;

import indexer.dataunit.Location;
import indexer.dataunit.node.*;
import indexer.visitor.definition.*;
import indexer.visitor.reference.*;

import java.io.File;
import java.util.HashMap;
import java.util.Vector;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;

public class Project {
    private String path;
    private Vector<ASTVisitor> astVisitors;
    public HashMap<String, ClassNode> projectRoot;


    public Project() {
        this.projectRoot = new HashMap<>();
    }

    public Project(String path) {
        this.path = path;
    }

    //for testing the data in the built project root
    public void test() {
        //for testing method table
        testingMethodTable();
        //TODO: testing import table
        //TODO: testing variable table
        //TODO: testing type table
    }

    public void testingMethodTable() {
        if (projectRoot.isEmpty()) {
            System.out.println("No Java File in Project " + path);
            System.exit(0);
        }
        for (Entry<String, ClassNode> classEntry : projectRoot.entrySet()) {
            System.out.println("#Class File " + classEntry.getKey() + "has Method Declarations: ");
            ClassNode classNode = classEntry.getValue();
            for (Entry<String, Location> methodEntry : classNode.methodTable.entrySet())
                System.out.println(methodEntry.getKey() + " at Line" + methodEntry.getValue().lineNumber);
        }
        System.out.println("Data is OK!");
    }

    public final int DEFINITION = 1;
    public final int REFERENCE = 2;

    public final int VARIABLE = 1;
    public final int IMPORT = 2;
    public final int TYPE = 4;//include class and interface
    public final int METHOD = 8;

    /*
    flag: Def or Ref, type: variable, class, method, etc.
     */
    public void exploreWithVisitor(int flag, int type) {
        for (Entry<String, ClassNode> classEntry : projectRoot.entrySet()) {
            ClassNode classNode = classEntry.getValue();
            CompilationUnit compilationUnit = buildCompilationUnit(classNode.getAbsolutePath());
            if ((flag & DEFINITION) != 0) {
                if ((type & IMPORT) != 0) {
                    ImportDefinitionVisitor astVisitor = new ImportDefinitionVisitor(classEntry.getValue());
                    compilationUnit.accept(astVisitor);
                }
                if ((type & METHOD) != 0) {
                    MethodDefinitionVisitor astVisitor = new MethodDefinitionVisitor(compilationUnit, classNode);
                    compilationUnit.accept(astVisitor);
                }
                if ((type & VARIABLE) != 0) {
                    VariableDefinitionVisitor astVisitor = new VariableDefinitionVisitor(compilationUnit, classNode);
                    compilationUnit.accept(astVisitor);
                }
                if ((type & TYPE) != 0) {
                    ClassDefinitionVisitor astVisitor = new ClassDefinitionVisitor(compilationUnit, classNode);
                    compilationUnit.accept(astVisitor);
                }
                if (type == 0)
                    System.exit(0);
            }
            if ((flag & REFERENCE) != 0) {
                if ((flag & DEFINITION) != 0) {
                    if ((type & IMPORT) != 0) {
                        //do nothing
                    }
                    if ((type & METHOD) != 0) {
                        MethodReferenceVisitor astVisitor = new MethodReferenceVisitor(compilationUnit, classNode);
                        compilationUnit.accept(astVisitor);
                    }
                    if ((type & VARIABLE) != 0) {
                    }

                    if ((type & TYPE) != 0) {
                    }
                    if (type == 0)
                        System.exit(0);
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
        //must be added, otherwise, the bindings can't be resolved.
        String[] sources = {"/Users/zhangxiaodong10/IdeaProjects/Symbol_Table/src/"};
        String[] classpath = {"/Library/Java/JavaVirtualMachines/jdk1.8.0_172.jdk/Contents/Home/jre/librt.jar"};
        parser.setEnvironment(classpath, sources, new String[]{"UTF-8"}, true);
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
            buildClassList(new File(path));
        } else {
            String className = file.getName().split(".")[0];
            projectRoot.put(file.getAbsolutePath(), new ClassNode(className, path));
        }
        return true;
    }

    /*
    recursion
     */
    private void buildClassList(File file) {
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
                        buildClassList(subfile);
                    } else {
                        if (subfile.getName().endsWith("java")) {
//                            System.out.println(subfile.getName() + ";" + subfile.getAbsolutePath());
                            String className = subfile.getName().split("\\.")[0];
                            projectRoot.put(subfile.getAbsolutePath(), new ClassNode(className, subfile.getAbsolutePath()));
                        }
                    }
                }
            }
        } else {
            System.out.println(file.getName() + " does not exist!");
        }
    }

}
