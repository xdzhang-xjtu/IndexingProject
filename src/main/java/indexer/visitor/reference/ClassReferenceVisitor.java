package indexer.visitor.reference;

import indexer.Indexing;
import indexer.dataunit.ClassNode;
import indexer.dataunit.Location;
import indexer.query.Query;
import org.eclipse.jdt.core.dom.*;

import java.util.Vector;

public class ClassReferenceVisitor extends ASTVisitor {
    CompilationUnit compilationUnit;
    ClassNode classNode;

    public ClassReferenceVisitor(CompilationUnit compilationUnit, ClassNode classNode) {
        this.compilationUnit = compilationUnit;
        this.classNode = classNode;
    }

    public boolean visit(SimpleType node) {
        Indexing.statistics.TYPE_REF++;
        Name name = node.getName();
        ITypeBinding iTypeBinding = node.resolveBinding();
        if (iTypeBinding == null) {
            printException(classNode.getUrl(), name.getFullyQualifiedName(),
                    compilationUnit.getLineNumber(name.getStartPosition()));
        } else {
            if (!iTypeBinding.isFromSource()) {
                Indexing.statistics.CLASS_REF_EXTERNAL++;
                printExternalType(classNode.getUrl(), name.getFullyQualifiedName(),
                        compilationUnit.getLineNumber(name.getStartPosition()));
            } else {
                String packageName = iTypeBinding.getPackage().getName();

                Query query = new Query();
                //require absolute path, import table, and package name from classNade.
                query.setTypeQueryScope(name.getFullyQualifiedName(), packageName);
                query.searchType();
                if ((query.queryResult.size() == 0)) {
                    Indexing.statistics.TYPE_REF_NOT_FOUND++;
                } else {
                    Indexing.statistics.CLASS_REF_INTERNAL++;
                }
                int line = compilationUnit.getLineNumber(name.getStartPosition());
                printTypeRefRelation(classNode.getUrl(), name.getFullyQualifiedName(), line, query.queryResult);
            }
        }
        return true;
    }

    public void printTypeRefRelation(String path, String name, int lineNumber, Vector<Location> result) {
        System.err.print("文件" + path + " # ");
        System.err.print("行" + lineNumber + " # ");
        System.err.print("类型引用" + name + " # ");

        if ((result.size() == 0)) {
            System.err.println("外部类型");
        } else {
            System.err.print("定义@");
            System.err.println(result);
            if (result.size() >= 2) {
                System.err.print(" ERROR: Multiple defs for a type ref!");
                System.exit(0);
            }
        }
    }

    public void printException(String path, String name, int lineNumber) {
        System.err.print("文件" + path + " # ");
        System.err.print("行" + lineNumber + " # ");
        System.err.print("类型引用" + name + " # ");
        System.err.println("**Exception: Null Bindings!");
    }

    public void printExternalType(String path, String name, int lineNumber) {
        System.err.print("文件" + path + " # ");
        System.err.print("行" + lineNumber + " # ");
        System.err.print("类型引用" + name + " # ");
        System.err.println("外部类型");
    }
}
