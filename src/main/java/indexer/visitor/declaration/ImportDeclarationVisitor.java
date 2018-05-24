package indexer.visitor.declaration;

import indexer.Indexing;
import indexer.dataunit.ClassNode;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.Name;

import java.util.Arrays;
import java.util.Vector;

public class ImportDeclarationVisitor extends ASTVisitor {
    ClassNode classNode;

    public ImportDeclarationVisitor(ClassNode classNode) {
        this.classNode = classNode;
    }

    public boolean visit(ImportDeclaration node) {

        Vector<String> externalLibList = new Vector<>(Arrays.asList(Indexing.externalLibs));
        Name name = node.getName();
        if (externalLibList.contains(name.getFullyQualifiedName()))
            return true;

        classNode.importTable.add(name.getFullyQualifiedName());
        return true;
    }
}
