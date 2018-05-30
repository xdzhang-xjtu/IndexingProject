package indexer.project;

import indexer.Indexing;
import indexer.dataunit.ClassNode;
import indexer.visitor.declaration.*;
import indexer.visitor.reference.*;

import java.io.File;
import java.util.HashMap;
import java.io.IOException;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.io.FileUtils;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;

public class Project {
    private String projectPath;
    private String sourcePath;
    public Vector<String> javaFiles;
    public HashMap<String, Vector<ClassNode>> projectData;

    public Project() {
        this.projectData = new HashMap<>();
        this.javaFiles = new Vector<>();
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
        for (String entry : javaFiles) {
            CompilationUnit compilationUnit = buildCompilationUnit(entry);
            if ((type & TYPE) != 0) {
                ClassDeclarationVisitor astVisitor = new ClassDeclarationVisitor(compilationUnit, entry);
                compilationUnit.accept(astVisitor);
            }
            if ((type & IMPORT) != 0) {
            }
            if ((type & PACKAGE) != 0) {
            }
            if ((type & METHOD) != 0) {
//                MethodDeclarationVisitor astVisitor = new MethodDeclarationVisitor(compilationUnit);
//                compilationUnit.accept(astVisitor);
            }
            if ((type & VARIABLE) != 0) {
                //Not implemented yet
//                VariableDeclarationVisitor astVisitor = new VariableDeclarationVisitor(compilationUnit);
//                compilationUnit.accept(astVisitor);
            }
            if (type == 0) {
                System.err.println("ERROR: IN method Project.applyDeclarationVisitor");
                System.exit(0);
            }
        }
    }

    public void applyReferenceVisitor(int type) {
        for (String entry : javaFiles) {
            CompilationUnit compilationUnit = buildCompilationUnit(entry);
            if ((type & METHOD) != 0) {
//                MethodReferenceVisitor astVisitor = new MethodReferenceVisitor(compilationUnit);
//                compilationUnit.accept(astVisitor);
            }
            if ((type & VARIABLE) != 0) {
            }
            if ((type & TYPE) != 0) {
                ClassReferenceVisitor astVisitor = new ClassReferenceVisitor(compilationUnit, entry);
                compilationUnit.accept(astVisitor);
            }
            if (type == 0) {
                System.err.println("ERROR: IN method Project.applyDeclarationVisitor");
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
        String[] classpath = {
//                "/Library/Java/JavaVirtualMachines/jdk1.8.0_172.jdk/Contents/Home/jre/lib/rt.jar",
                "/Library/Java/JavaVirtualMachines/jdk1.8.0_171.jdk/Contents/Home/jre/lib/rt.jar"
        };
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
            buildJavaFileList(new File(projectPath));
        } else {
            String className = file.getName().split("\\.")[0];

            javaFiles.add(file.getAbsolutePath());
        }
        return true;
    }

    /*
    recursion
     */
    private void buildJavaFileList(File file) {
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
                        buildJavaFileList(subfile);
                    } else {
                        if (subfile.getName().endsWith(".java")) {
                            javaFiles.add(subfile.getAbsolutePath());
//                            String className = subfile.getName().split("\\.")[0];
                        }
                    }
                }
            }
        } else {
            System.err.println(file.getName() + " does not exist!");
        }
    }
}
