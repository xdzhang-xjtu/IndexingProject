package indexer.visitor.reference;

import indexer.*;
import indexer.dataunit.Location;
import indexer.dataunit.node.ClassNode;
import indexer.dataunit.node.DirNode;
import indexer.dataunit.node.Node;
import indexer.dataunit.project.ProjectTree;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.MethodInvocation;

import java.util.Vector;

public class MethodReferenceVisitor extends ASTVisitor {
    CompilationUnit compilationUnit;
    ClassNode classNode;

    public MethodReferenceVisitor(CompilationUnit compilationUnit, ClassNode classNode) {
        this.compilationUnit = compilationUnit;
        this.classNode = classNode;
    }

    public boolean visit(MethodInvocation node) {
        SimpleName name = node.getName();
        Vector<Location> result = new Vector<>();
        globalSearch(Indexing.project.projectRoot, name.getIdentifier(), result);
        System.out.println("\n#Invocation of '" + name + "' at line " +
                compilationUnit.getLineNumber(name.getStartPosition()) + ", its Definition is ");
        if ((result.size() == 0))
            System.out.println("out of this project!");
        else
            for (Location location : result) {
                System.out.println(location);
            }
        return true;
    }

    public void globalSearch(Node node, String name, Vector<Location> result) {
        if (node instanceof ClassNode) {

            if (((ClassNode) node).methodTable.containsKey(name)) {
                result.add(((ClassNode) node).methodTable.get(name));
            }
        } else {
            for (Node subNode : ((DirNode) node).getChild()) {
                if (subNode instanceof ClassNode) {
                    if (((ClassNode) subNode).methodTable.containsKey(name)) {
                        result.add(((ClassNode) subNode).methodTable.get(name));
                    }
                } else {
                    globalSearch(subNode, name, result);
                }
            }
        }
    }
}
