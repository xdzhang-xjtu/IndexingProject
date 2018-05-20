package indexer.visitor.definition;

import indexer.dataunit.node.ClassNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;

public class ClassDefinitionVisitor extends ASTVisitor {
    CompilationUnit compilationUnit;
    ClassNode classNode;

    public ClassDefinitionVisitor(CompilationUnit compilationUnit, ClassNode classNode) {
        this.compilationUnit = compilationUnit;
        this.classNode = classNode;
    }

    public boolean visit(TypeDeclaration node) {
//        SimpleName name = node.getName();
//        String line = String.valueOf(compilationUnit.getLineNumber(name.getStartPosition()));
//        classNode.methodTable.put(name.getIdentifier(), line);

        return true;
    }
}
