package indexer.visitor.reference;

import indexer.Indexing;
import indexer.dataunit.Location;
import indexer.dataunit.ClassNode;
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

    public void print(String path, String name, int lineNumber, int type) {
        System.err.print("文件" + path + " # ");
        System.err.print("行" + lineNumber + " # ");
        System.err.print("类型引用" + name + " # ");
        if (type == 1) {
            System.err.println("**Exception: Null Bindings!");
        } else if (type == 2) {
            System.err.println("调用外部函数");
        } else if (type == 3) {
            System.err.println("Unfound");
        } else {
            System.err.print(" ERROR: Wrong print type!");
            System.exit(0);
        }
    }

    public boolean visit(MethodInvocation node) {
        Indexing.statistics.CALL++;
        SimpleName name = node.getName();
        int line = compilationUnit.getLineNumber(name.getStartPosition());
        if (!Indexing.DEBUG)
            System.err.println(name.getIdentifier());
        if (node.resolveMethodBinding() == null) {
            Indexing.statistics.EXCEPTION_NULL_BINDING_METHOD++;
//            print(classNode.getUrl(), name.getIdentifier(), line, 1);
        } else {
            ITypeBinding iTypeBinding = node.resolveMethodBinding().getDeclaringClass();
            if (!iTypeBinding.isFromSource()) {
                Indexing.statistics.EXTERNAL_CALL++;
//                print(classNode.getUrl(), name.getIdentifier(), line, 2);
            } else {
                String destPackage;
                if (iTypeBinding.getPackage() == null) {
                    if (Indexing.DEBUG) {
                        System.err.println("Cannot resolve the package of declaring class");
                        System.exit(0);
                    }
                    destPackage = "";// thus, perform no restrict
                } else {
                    destPackage = iTypeBinding.getPackage().getName();
                }
                String declaringClassName = iTypeBinding.getName();
                //customize the query, by obtaining some info from CompilationUnit and I*Bindings
                Query query = new Query();
                query.setMethodQueryScope(name.getIdentifier(), destPackage, declaringClassName);

                if (!Indexing.DEBUG)
                    query.brutallySearchMethod();//for testing
                else
                    query.searchMethod();

                if ((query.queryResult.size() == 0)) {
                    System.err.println(name.getIdentifier() + "---" + destPackage + "---" + declaringClassName);
                    Indexing.statistics.CALL_NOT_FOUND++;
                    print(classNode.getUrl(), name.getIdentifier(), line, 3);
                } else {
                    Indexing.statistics.INTERNAL_CALL++;
                    if (query.queryResult.size() >= 2) {
                        Indexing.statistics.EXCEPTION_MULTI_DEFS++;
                    }
                }
                //Here, we also can record the data.
//                printCallRelation(classNode.getUrl(), name.getIdentifier(),
//                        compilationUnit.getLineNumber(name.getStartPosition()), query.queryResult);
            }
        }
        return true;
    }
}
