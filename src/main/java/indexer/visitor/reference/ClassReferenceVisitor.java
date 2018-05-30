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
        int line = compilationUnit.getLineNumber(name.getStartPosition());
        ITypeBinding iTypeBinding = node.resolveBinding();
        if (iTypeBinding == null) {
//            print(classNode.getUrl(), name.getFullyQualifiedName(), line, 1);
        } else {
            if (!iTypeBinding.isFromSource()) {
                Indexing.statistics.CLASS_REF_EXTERNAL++;
//                print(classNode.getUrl(), name.getFullyQualifiedName(), line, 2);
            } else {
//                System.err.println(classNode.getUrl() + ":" + line + ":" + name.getFullyQualifiedName());
                if (iTypeBinding.getPackage() != null) {
                    String packageName = iTypeBinding.getPackage().getName();
                    Query query = new Query();
                    if (iTypeBinding.isMember())
                        //require absolute path, import table, and package name from classNade.
                        query.setTypeQueryScope(name.getFullyQualifiedName(), packageName, true);
                    else if (iTypeBinding.isTopLevel())
                        query.setTypeQueryScope(name.getFullyQualifiedName(), packageName, false);
                    query.searchType();
                    if ((query.queryResult.size() == 0)) {
                        Indexing.statistics.TYPE_REF_NOT_FOUND++;
//                        print(classNode.getUrl(), name.getFullyQualifiedName(), line, 3);
                    } else {
                        Indexing.statistics.CLASS_REF_INTERNAL++;
                    }

//                printTypeRefRelation(classNode.getUrl(), name.getFullyQualifiedName(), line, query.queryResult);
                }else {
                    Indexing.statistics.TYPE_REF_NOT_FOUND++;
//                    print(classNode.getUrl(), name.getFullyQualifiedName(), line, 3);
                }
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

    public void print(String path, String name, int lineNumber, int type) {
        System.err.print("文件" + path + " # ");
        System.err.print("行" + lineNumber + " # ");
        System.err.print("类型引用" + name + " # ");
        if (type == 1) {
            System.err.println("**Exception: Null Bindings!");
        } else if (type == 2) {
            System.err.println("外部类型");
        } else if (type == 3) {
            System.err.println("Unfound");
        } else {
            System.err.print(" ERROR: Wrong print type!");
            System.exit(0);
        }
    }
}
