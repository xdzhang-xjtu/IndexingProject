package indexer.visitor.declaration;

import indexer.Indexing;
import indexer.dataunit.ClassNode;
import indexer.dataunit.Location;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;

import java.util.HashMap;

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
        if (node.resolveBinding().isMember()) {//inner class
            Indexing.statistics.DECLARING_INNER_CLASS++;
            classNode.hasInnerClass = true;
            classNode.innerClassTable.put(name.getIdentifier(), location);
        } else if (node.resolveBinding().isTopLevel()) {//top level class
            Indexing.statistics.DECLARING_TOP_CLASS++;
            classNode.classLocation = location;
        } else {
            System.err.println("EXCEPTION: in ClassDeclarationVisitor.visit");
        }
        if (!Indexing.DEBUG) {
            String string;
            if (node.resolveBinding().isMember())
                string = "内部类";
            else
                string = "非内部类";
            System.err.println(classNode.getUrl() + " : " + name.getIdentifier() + " @ " + location + "--" + string);
        }
        return true;
    }
}
