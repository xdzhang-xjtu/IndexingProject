package indexer.visitor.declaration;

import indexer.dataunit.node.ClassNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;

public class PackageDeclarationVisitor extends ASTVisitor {
    ClassNode classNode;

    public PackageDeclarationVisitor(ClassNode classNode) {
        this.classNode = classNode;
    }
    public boolean	visit(PackageDeclaration node){
        Name name = node.getName();
        classNode.setPackageStr(name.getFullyQualifiedName());
        return true;
    }

}
