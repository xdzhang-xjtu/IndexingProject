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

    public void printInfoToConsole(String path, String name, int lineNumber, Vector<Location> result) {
        System.err.print("文件" + path + " # ");
        System.err.print("行" + lineNumber + " # ");
        System.err.print("调用" + name + " # ");

        if ((result.size() == 0)) {
            Indexing.statistics.EXTERNAL_CALL++;
            System.err.println("外部函数");
        } else {
            Indexing.statistics.INTERNAL_CALL++;
            System.err.print("定义@");
            System.err.println(result);

            if (result.size() >= 2) {
                if (Indexing.DEBUG) {
                    System.err.print(" ERROR: Multiple Defs!");
                    System.exit(0);
                }
                Indexing.statistics.EXCEPTION_MULTI_DEFS++;
            }
        }
    }

    public void printErrorToConsole(String path, String name, int lineNumber) {
        System.err.print("文件" + path + " # ");
        System.err.print("行" + lineNumber + " # ");
        System.err.print("调用" + name + " # ");
        System.err.println("**ERROR: Null Bindings!");
    }

    public boolean visit(MethodInvocation node) {
        Indexing.statistics.CALL++;
        SimpleName name = node.getName();
        if (!Indexing.DEBUG)
            System.err.println(name.getIdentifier());
        if (node.resolveMethodBinding() == null) {
            Indexing.statistics.EXCEPTION_NULL_BINGDING++;
            if (Indexing.DEBUG) {
                printErrorToConsole(classNode.getUrl(), name.getIdentifier(),
                        compilationUnit.getLineNumber(name.getStartPosition()));
//                System.exit(0);
            }
        } else {
            ITypeBinding iTypeBinding = node.resolveMethodBinding().getDeclaringClass();
            if (iTypeBinding.getPackage()==null){
                System.err.println("Cannot resolve the package of decalaring class");
                System.exit(0);
            }else {
                System.err.println("The resolved package is " + iTypeBinding.getPackage().getName());
            }
            String declaringClassName = iTypeBinding.getName();
            //customize the query, by obtaining some info from CompilationUnit and I*Bindings
            Query query = new Query();
            //require absolute pat , import table, and package name from classNade.
            query.setQueryScope(name.getIdentifier(), classNode.getPackageStr(),
                    declaringClassName, classNode.getAbsolutePath(), classNode.importTable);

            if (!Indexing.DEBUG)
                query.brutallySearch();
            else
                query.search();

            //Here, we also can record the data.
            if (Indexing.DEBUG)
                printInfoToConsole(classNode.getUrl(), name.getIdentifier(),
                        compilationUnit.getLineNumber(name.getStartPosition()), query.queryResult);
        }
        return true;
    }
}
