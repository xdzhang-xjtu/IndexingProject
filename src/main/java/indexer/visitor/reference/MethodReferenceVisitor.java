package indexer.visitor.reference;

import indexer.Indexing;
import indexer.dataunit.Location;
import indexer.dataunit.node.ClassNode;
import indexer.query.Query;
import org.eclipse.jdt.core.dom.*;

import java.util.Vector;

public class MethodReferenceVisitor extends ASTVisitor {
    CompilationUnit compilationUnit;
    ClassNode classNode;

    public MethodReferenceVisitor(CompilationUnit compilationUnit, ClassNode classNode) {
        this.compilationUnit = compilationUnit;
        this.classNode = classNode;
    }

    public void printCallRelation(String path, String name, int lineNumber, Vector<Location> result) {
        System.err.print("文件" + path + " # ");
        System.err.print("行" + lineNumber + " # ");
        System.err.print("调用" + name + " # ");

        if ((result.size() == 0)) {
            System.err.println("调用外部函数");
        } else {
            System.err.print("定义@");
            System.err.println(result);

            if (result.size() >= 2) {
                System.err.print(" ERROR: Multiple Defs!");
            }
        }
    }

    public void printException(String path, String name, int lineNumber) {
        System.err.print("文件" + path + " # ");
        System.err.print("行" + lineNumber + " # ");
        System.err.print("调用" + name + " # ");
        System.err.println("**ERROR: Null Bindings!");
    }

    public void printExternalMethod(String path, String name, int lineNumber) {
        System.err.print("文件" + path + " # ");
        System.err.print("行" + lineNumber + " # ");
        System.err.print("调用" + name + " # ");
        System.err.println("调用外部函数");
    }

    public boolean visit(MethodInvocation node) {
        Indexing.statistics.CALL++;
        SimpleName name = node.getName();
        if (!Indexing.DEBUG)
            System.err.println(name.getIdentifier());
        if (node.resolveMethodBinding() == null) {
            Indexing.statistics.EXCEPTION_NULL_BINGDING++;
            printException(classNode.getUrl(), name.getIdentifier(),
                    compilationUnit.getLineNumber(name.getStartPosition()));
//                System.exit(0);
        } else {
            ITypeBinding iTypeBinding = node.resolveMethodBinding().getDeclaringClass();
            String destPackage;
            if (iTypeBinding.getPackage() == null) {
                if (!Indexing.DEBUG) {
                    System.err.println("Cannot resolve the package of declaring class");
                    System.exit(0);
                }
                destPackage = "";// thus, perform no restrict
            } else {
                destPackage = iTypeBinding.getPackage().getName();
            }

            if (isExternalMethod(destPackage)) {
                Indexing.statistics.EXTERNAL_CALL++;
                printExternalMethod(classNode.getUrl(), name.getIdentifier(),
                        compilationUnit.getLineNumber(name.getStartPosition()));
            } else {
                String declaringClassName = iTypeBinding.getName();
                //customize the query, by obtaining some info from CompilationUnit and I*Bindings
                Query query = new Query();
                //require absolute path, import table, and package name from classNade.
                query.setQueryScope(name.getIdentifier(), classNode.getPackageStr(),
                        declaringClassName, classNode.getAbsolutePath(), classNode.importTable);
                query.setQueryScope(name.getIdentifier(), destPackage, declaringClassName);

                if (!Indexing.DEBUG)
                    query.brutallySearch();
                else
                    query.search_v2();

                if ((query.queryResult.size() == 0)) {
                    Indexing.statistics.EXTERNAL_CALL++;
                } else {
                    Indexing.statistics.INTERNAL_CALL++;
                    if (query.queryResult.size() >= 2) {
                        Indexing.statistics.EXCEPTION_MULTI_DEFS++;
                    }
                }
                //Here, we also can record the data.
                printCallRelation(classNode.getUrl(), name.getIdentifier(),
                        compilationUnit.getLineNumber(name.getStartPosition()), query.queryResult);
            }
        }
        return true;
    }

    private boolean isExternalMethod(String destPackage) {
        for (String str : Indexing.externalLibs) {
            if (str.equals(destPackage))
                return true;
        }
        return false;
    }
}
