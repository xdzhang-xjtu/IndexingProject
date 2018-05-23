package indexer.visitor.reference;

import indexer.Indexing;
import indexer.dataunit.Location;
import indexer.dataunit.node.ClassNode;
import indexer.query.Query;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.MethodInvocation;

import java.util.Vector;

public class MethodReferenceVisitor extends ASTVisitor {
    CompilationUnit compilationUnit;
    ClassNode classNode;

    public MethodReferenceVisitor(CompilationUnit compilationUnit, ClassNode classNode) {
        this.compilationUnit = compilationUnit;
        this.classNode = classNode;
    }

    public void printInfoToConsole(String path, String name, int lineNumber, Vector<Location> result) {
        System.out.print("文件" + path + " # ");
        System.out.print("行" + lineNumber + " # ");
        System.out.print("方法调用" + name + " # ");

        if ((result.size() == 0)) {
            Indexing.statistics.EXTERNAL_CALL++;
            System.out.println("外部函数");
        }
        else {
            Indexing.statistics.INTERNAL_CALL++;
            System.out.print("@");
            if (result.size()>2){
                Indexing.statistics.EXCEPTION_MULTI_DEFS ++;
            }
            System.out.println(result);
        }
    }

    public void printErrorToConsole(String path, String name, int lineNumber) {
        System.out.print("文件" + path + " # ");
        System.out.print("行" + lineNumber + " # ");
        System.out.print("方法调用" + name + " # ");
        System.out.println("**ERROR: Null Bindings!");
    }

    public boolean visit(MethodInvocation node) {
        Indexing.statistics.CALL++;
        SimpleName name = node.getName();
        if (!Indexing.DEBUG)
            System.out.println(name.getIdentifier());
        if (node.resolveMethodBinding() == null) {
            Indexing.statistics.EXCEPTION_NULL_BINGDING++;
            if (Indexing.DEBUG) {
                printErrorToConsole(classNode.getAbsolutePath(), name.getIdentifier(),
                        compilationUnit.getLineNumber(name.getStartPosition()));
//                System.exit(0);
            }
        } else {
            String declaringClassName = node.resolveMethodBinding().getDeclaringClass().getName();
            //customize the query, by obtaining some info from CompilationUnit and I*Bindings
            Query query = new Query();
            //require absolute pat , import table, and package name from classNade.
            query.setQueryScope(name.getIdentifier(), classNode.getPackageStr(),
                    declaringClassName, classNode.getAbsolutePath(), classNode.importTable);

            if (Indexing.DEBUG)
                query.brutallySearch();
            else
                query.search();

            //Here, we also can record the data.
            if (Indexing.DEBUG)
                printInfoToConsole(classNode.getAbsolutePath(), name.getIdentifier(),
                        compilationUnit.getLineNumber(name.getStartPosition()), query.queryResult);
        }
        return true;
    }
}
