package indexer.visitor.reference;

import indexer.*;
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
    Query query;

    public MethodReferenceVisitor(CompilationUnit compilationUnit, ClassNode classNode) {
        this.compilationUnit = compilationUnit;
        this.classNode = classNode;
        this.query = new Query();
    }

    public void printToConsole(String name, int lineNumber, Vector<Location> result) {
        System.out.println("\n#Invocation of '" + name + "' at line " +
                lineNumber + ", its Definition is ");
        if ((result.size() == 0))
            System.out.println("out of this project!");
        else
            for (Location location : result) {
                System.out.println(location);
            }
    }

    public boolean visit(MethodInvocation node) {
        SimpleName name = node.getName();
        String declaringClassName = node.resolveMethodBinding().getDeclaringClass().getName();
        Vector<Location> result = new Vector<>();
        //customize the query, by obtaining some info from CompilationUnit and I*Bindings
        //Todo: customize the query
        Query query = new Query();
        query.setQueryScope(name.getIdentifier(),declaringClassName, classNode.importTable);
        query.search(Indexing.project.projectRoot);

        //without any scope restrict
//        Search(Indexing.project.projectRoot, name.getIdentifier(), result);

        //Here, we also can record the data.
        printToConsole(name.getIdentifier(), compilationUnit.getLineNumber(name.getStartPosition()), result);
        //
        return true;
    }


}
