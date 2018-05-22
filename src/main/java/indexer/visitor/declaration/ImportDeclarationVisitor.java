package indexer.visitor.declaration;

import indexer.dataunit.node.ClassNode;
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
        String[] externalLibs = {
                "java.applet",
                "java.awt",
                "java.beans",
                "java.io",
                "java.lang",
                "java.math",
                "java.net",
                "java.rmi",
                "java.security",
                "java.sql",
                "java.text",
                "java.util",
                "javax.accessibility",
                "javax.naming",
                "javax.rmi",
                "javax.sound",
                "javax.swing"};
        Vector<String> externalLibList = new Vector<>(Arrays.asList(externalLibs));
        Name name = node.getName();
        if (externalLibList.contains(name.getFullyQualifiedName()))
            return true;

        classNode.importTable.add(name.getFullyQualifiedName());
        return true;
    }
}
