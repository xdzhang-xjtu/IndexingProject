package indexer.project;

import indexer.Indexing;
import indexer.dataunit.Location;
import indexer.dataunit.node.*;
import indexer.visitor.declaration.*;
import indexer.visitor.reference.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;

public class Project {
    private String projectPath;
    private String sourcePath;
    public HashMap<String, ClassNode> projectData;


    public Project() {
        this.projectData = new HashMap<>();
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
        if (projectData.isEmpty()) {
            System.err.println("ERROR: No Java File in Project " + projectPath);
            System.exit(0);
        }
        for (Entry<String, ClassNode> classEntry : projectData.entrySet()) {
            System.err.println("#Class File " + classEntry.getKey() + "\nMethod Declarations: ");
            ClassNode classNode = classEntry.getValue();
            if (!Indexing.DEBUG)
                if (classNode.methodTable.isEmpty()) {
                    System.err.println("ERROR: Method Table is empty!");
                    System.exit(0);
                }
            for (Entry<String, Location> methodEntry : classNode.methodTable.entrySet())
                System.err.println(methodEntry.getKey() + " at Line " + methodEntry.getValue().lineNumber);
        }
        System.err.println("Data is OK!");
    }

    public final int VARIABLE = 1;
    public final int IMPORT = 2;
    public final int TYPE = 4;//include class and interface
    public final int METHOD = 8;
    public final int PACKAGE = 16;

    /*
    flag: Def or Ref, type: variable, class, method, etc.
     */
    public void applyDeclarationVisitor(int type) {
        for (Entry<String, ClassNode> classEntry : projectData.entrySet()) {
            ClassNode classNode = classEntry.getValue();
            CompilationUnit compilationUnit = buildCompilationUnit(classNode.getAbsolutePath());
//            System.err.println("xxxxxxxx" + classNode.getAbsolutePath());
            if ((type & IMPORT) != 0) {
                PackageDeclarationVisitor astVisitor = new PackageDeclarationVisitor(classEntry.getValue());
                compilationUnit.accept(astVisitor);
            }
            if ((type & PACKAGE) != 0) {
                ImportDeclarationVisitor astVisitor = new ImportDeclarationVisitor(classEntry.getValue());
                compilationUnit.accept(astVisitor);
            }
            if ((type & METHOD) != 0) {
                MethodDeclarationVisitor astVisitor = new MethodDeclarationVisitor(compilationUnit, classNode);
                compilationUnit.accept(astVisitor);
            }
            if ((type & VARIABLE) != 0) {
                //Not implemented yet
                VariableDeclarationVisitor astVisitor = new VariableDeclarationVisitor(compilationUnit, classNode);
                compilationUnit.accept(astVisitor);
            }
            if ((type & TYPE) != 0) {
                //Not implemented yet
                ClassDeclarationVisitor astVisitor = new ClassDeclarationVisitor(compilationUnit, classNode);
                compilationUnit.accept(astVisitor);
            }
            if (type == 0) {
                System.err.println("ERROR: IN method applyDeclarationVisitor");
                System.exit(0);
            }
        }
    }

    public void applyReferenceVisitor(int type) {
        for (Entry<String, ClassNode> classEntry : projectData.entrySet()) {
            ClassNode classNode = classEntry.getValue();
            CompilationUnit compilationUnit = buildCompilationUnit(classNode.getAbsolutePath());
//            System.err.println("xxxxxxxx" + classNode.getAbsolutePath());
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
            if (type == 0) {
                System.err.println("ERROR: IN method applyReferenceVisitor");
                System.exit(0);
            }
        }
    }


    public CompilationUnit buildCompilationUnit(String absolutePath) {
        File file = new File(absolutePath);
        String str = "";
        try {
            str = FileUtils.readFileToString(file, "UTF-8");
        } catch (IOException e) {
            System.err.println(e);
        }

        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setResolveBindings(true);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setBindingsRecovery(true);
        Map options = JavaCore.getOptions();
        parser.setCompilerOptions(options);
        parser.setUnitName(file.getName());

        //should automatically extract the source paths from project path.
        //in the future work, need to implement it.
        String[] sources = {sourcePath};
        //should automatically extract librt.jar from system
        String[] classpath = {"/Library/Java/JavaVirtualMachines/jdk1.8.0_172.jdk/Contents/Home/jre/librt.jar"};
        //must be added, otherwise, the bindings can't be resolved.
        parser.setEnvironment(classpath, sources, new String[]{"UTF-8"}, true);
        parser.setSource(str.toCharArray());
        return (CompilationUnit) parser.createAST(null);
    }

    public void setPaths(String projectPath, String sourcePath) {
        this.projectPath = projectPath;
        this.sourcePath = sourcePath;
    }

    public boolean initialize() {
        File file = new File(projectPath);
        if (!file.exists()) {
            System.err.println("The project does not exist!");
            return false;
        }
        if (file.isDirectory()) {
            buildClassList(new File(projectPath));
        } else {
            String className = file.getName().split("\\.")[0];
            projectData.put(file.getAbsolutePath(), new ClassNode(className, file.getName(), projectPath));
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
                System.err.println(file.getName() + " is empty!");
            } else {
                for (File subfile : files) {
                    if (subfile.getName().startsWith(".")) {//filter out the .* dirs
                        continue;
                    }
                    if (subfile.isDirectory()) {
                        buildClassList(subfile);
                    } else {
                        if (subfile.getName().endsWith("java")) {
                            String className = subfile.getName().split("\\.")[0];
                            String abusolutePath = subfile.getAbsolutePath();
                            projectData.put(subfile.getAbsolutePath(), new ClassNode(className,
                                    abusolutePath.substring(projectPath.length()), subfile.getAbsolutePath()));
                        }
                    }
                }
            }
        } else {
            System.err.println(file.getName() + " does not exist!");
        }
    }

}
