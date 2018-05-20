package indexer.visitor.definition;

import indexer.Indexing;
import indexer.dataunit.node.ClassNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.*;


public class MethodDefinitionVisitor extends ASTVisitor {
    CompilationUnit compilationUnit;
    ClassNode classNode;

    public MethodDefinitionVisitor(CompilationUnit compilationUnit, ClassNode classNode) {
        this.compilationUnit = compilationUnit;
        this.classNode = classNode;
    }

    public boolean visit(MethodDeclaration node) {
        SimpleName name = node.getName();
        String line = String.valueOf(compilationUnit.getLineNumber(name.getStartPosition()));
        classNode.methodTable.put(name.getIdentifier(), line);

        return true;
    }
}