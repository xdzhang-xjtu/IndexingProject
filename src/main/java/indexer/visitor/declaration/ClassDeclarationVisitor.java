package indexer.visitor.declaration;

import indexer.Indexing;
import indexer.dataunit.ClassNode;
import indexer.dataunit.Location;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;

public class ClassDeclarationVisitor extends ASTVisitor {
    CompilationUnit compilationUnit;
    ClassNode classNode;

    public ClassDeclarationVisitor(CompilationUnit compilationUnit, ClassNode classNode) {
        this.compilationUnit = compilationUnit;
        this.classNode = classNode;
    }

    public boolean visit(TypeDeclaration node) {
        SimpleName name = node.getName();
        int line = compilationUnit.getLineNumber(name.getStartPosition());
        Location location = new Location(line, classNode.getUrl());
        //other cases to be handled
        if (!classNode.getName().equals(name.getIdentifier())) {
            System.err.println("ERROR: File name with class name are different!" +
                    classNode.getName() + "!=" + name.getIdentifier());
            System.exit(0);
        }
        Indexing.statistics.DECLARING_CLASS++;
        classNode.classLocation = location;
        if (!Indexing.DEBUG)
            System.err.println(classNode.getName() + " : " + name.getIdentifier() + " @ " + location);
        return true;
    }
}
