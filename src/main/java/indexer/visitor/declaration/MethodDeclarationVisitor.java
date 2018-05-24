package indexer.visitor.declaration;

import indexer.Indexing;
import indexer.dataunit.Location;
import indexer.dataunit.ClassNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.*;


public class MethodDeclarationVisitor extends ASTVisitor {
    CompilationUnit compilationUnit;
    ClassNode classNode;

    public MethodDeclarationVisitor(CompilationUnit compilationUnit, ClassNode classNode) {
        this.compilationUnit = compilationUnit;
        this.classNode = classNode;
    }

    public boolean visit(MethodDeclaration node) {
        SimpleName name = node.getName();
        int line = compilationUnit.getLineNumber(name.getStartPosition());
        Location location = new Location(line, classNode.getUrl());
        if (!Indexing.DEBUG)
            System.err.println(classNode.getName() + " : " + name.getIdentifier() + " @ " + location);
        classNode.methodTable.put(name.getIdentifier(), location);
        return true;
    }

}