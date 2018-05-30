package indexer.visitor.declaration;

import indexer.Indexing;
import indexer.dataunit.ClassNode;
import indexer.dataunit.Location;
import org.eclipse.jdt.core.dom.*;

import java.util.Vector;

public class ClassDeclarationVisitor extends ASTVisitor {
    CompilationUnit compilationUnit;
    String path;

    public ClassDeclarationVisitor(CompilationUnit compilationUnit, String path) {
        this.compilationUnit = compilationUnit;
        this.path = path;
    }

    public boolean visit(TypeDeclaration node) {
        SimpleName name = node.getName();
        String typeName = name.getFullyQualifiedName();
        int line = compilationUnit.getLineNumber(name.getStartPosition());
        ITypeBinding iTypeBinding = node.resolveBinding();
        String packageName = iTypeBinding.getPackage().getName();
        Location location = new Location(line, path);

        if (iTypeBinding.isClass() || iTypeBinding.isInterface()) {//class
            if (iTypeBinding.isTopLevel()) {//top level class
                ClassNode classNode = new ClassNode(typeName, packageName, path);
                classNode.classLocation = location;
                if (!Indexing.DEBUG) {
                    System.err.println(packageName + " : " + name.getIdentifier() + " @ " + line + " 顶层类");
                }
                //put all of declared types into innerClassTable
                ITypeBinding[] typeBindings = iTypeBinding.getDeclaredTypes();
                Indexing.statistics.MEMBER_TYPE_DECLARING += typeBindings.length;
                for (int i = 0; i < typeBindings.length; i++) {
                    ASTNode astNode = compilationUnit.findDeclaringNode(typeBindings[i]);
                    if (astNode == null) {
                        System.err.println("ERROR: Null Node!");
                        System.exit(0);
                    } else {
                        int declaredTypeLine = compilationUnit.getLineNumber(astNode.getStartPosition());
                        Location subLocation = new Location(declaredTypeLine, path);
                        String memberTypeName = typeBindings[i].getName();
                        classNode.innerClassTable.put(memberTypeName, subLocation);
                        if (Indexing.DEBUG) {
                            System.err.println(packageName + " : " + name.getIdentifier() + " : " +
                                    memberTypeName + " @ " + declaredTypeLine + " 内部类");
                        }
                    }
                }
                //
                if (!Indexing.project.projectData.containsKey(packageName)) {
                    Vector<ClassNode> classNodes = new Vector<>();
                    classNodes.add(classNode);
                    Indexing.project.projectData.put(packageName, classNodes);
                } else {
                    Vector<ClassNode> vec = Indexing.project.projectData.get(packageName);
                    if (!vec.contains(classNode))
                        vec.add(classNode);
                    else {
                        System.err.println("ERROR: Two classes have same name in one package!");
                        System.exit(0);
                    }
                }
                Indexing.statistics.TOP_TYPE_DECLARING++;
            }
        } else {
            System.err.println("EXCEPTION: Other types besides Class and Interface");
            System.exit(0);
        }
        return true;
    }
}
