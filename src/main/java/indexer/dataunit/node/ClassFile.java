package indexer.dataunit.node;

import indexer.dataunit.table.Table;

public class ClassFile implements Node {

    public String name;
    public String url;

    public ClassFile(String name, String url) {
        this.name = name;
        this.url = url;
    }

    private Table classTable;

    public Table getClassTable() {
        return classTable;
    }

    public void setClassTable(Table classTable) {
        this.classTable = classTable;
    }

    private Table definitionTable;

    public Table getDefinitionTable() {
        return definitionTable;
    }

    public void setDefinitionTable(Table definitionTable) {
        this.definitionTable = definitionTable;
    }

    private Table methodTable;

    public Table getMethodTable() {
        return methodTable;
    }

    public void setMethodTable(Table methodTable) {
        this.methodTable = methodTable;
    }
}