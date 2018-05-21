package indexer.visitor.definition;

import indexer.dataunit.node.ClassNode;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.Name;

public class ImportDefinitionVisitor extends ASTVisitor {
    ClassNode classNode;

    public ImportDefinitionVisitor(ClassNode classNode) {
        this.classNode = classNode;
    }

    public boolean visit(ImportDeclaration node) {
        Name name = node.getName();
        //need to filter out the external method
        if(name.getFullyQualifiedName().startsWith("java.util"))
            return  true;
        classNode.importTable.add(name.getFullyQualifiedName());
        return true;
    }
}
