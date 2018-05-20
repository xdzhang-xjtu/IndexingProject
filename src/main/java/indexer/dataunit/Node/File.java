package indexer.dataunit.Node;

import indexer.dataunit.Table.*;
import indexer.dataunit.Table.Table;

public class File extends Node {
    public File(String name) {
        super(name);
    }

    private ClassTable classTable;

    public ClassTable getClassTable() {
        return classTable;
    }

    public void setClassTable(ClassTable classTable) {
        this.classTable = classTable;
    }

    private DefinitionTable definitionTable;

    public DefinitionTable getDefinitionTable() {
        return definitionTable;
    }

    public void setDefinitionTable(DefinitionTable definitionTable) {
        this.definitionTable = definitionTable;
    }

    private MethodTable methodTable;

    public MethodTable getMethodTable() {
        return methodTable;
    }

    public void setMethodTable(MethodTable methodTable) {
        this.methodTable = methodTable;
    }
}
