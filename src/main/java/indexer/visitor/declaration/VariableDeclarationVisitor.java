package indexer.visitor.declaration;

import indexer.dataunit.ClassNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class VariableDeclarationVisitor extends ASTVisitor {
    CompilationUnit compilationUnit;
    ClassNode classNode;

    public VariableDeclarationVisitor(CompilationUnit compilationUnit, ClassNode classNode) {
        this.compilationUnit = compilationUnit;
        this.classNode = classNode;
    }

    public boolean visit(VariableDeclarationStatement node) {
//        SimpleName name = node.getName();
//        String line = String.valueOf(compilationUnit.getLineNumber(name.getStartPosition()));
//        classNode.methodTable.put(name.getIdentifier(), line);

        return true;
    }

}
