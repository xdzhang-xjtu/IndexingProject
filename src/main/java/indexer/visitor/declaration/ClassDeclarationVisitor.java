package indexer.visitor.declaration;

import indexer.Indexing;
import indexer.dataunit.ClassNode;
import indexer.dataunit.Location;
import org.eclipse.jdt.core.dom.*;

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
        ITypeBinding iTypeBinding = node.resolveBinding();
        if (iTypeBinding.isMember()) {//inner class
            Indexing.statistics.LOCAL_TYPE_DECLARING++;
            classNode.hasInnerClass = true;
            classNode.innerClassTable.put(name.getIdentifier(), location);
        } else if (node.resolveBinding().isTopLevel()) {//top level class
            Indexing.statistics.TOP_TYPE_DECLARING++;
            classNode.classLocation = location;
        } else {
            System.err.println("EXCEPTION: Unhandled private class or protected class " + name.getIdentifier() +
                    " in ClassDeclarationVisitor.visit");
        }
        if (!Indexing.DEBUG) {
            String string;
            if (node.resolveBinding().isMember())
                string = "内部类";
            else
                string = "公有类";
            System.err.println(classNode.getUrl() + " : " + name.getIdentifier() + " @ " + location + "--" + string);
        }
        return true;
    }
}
