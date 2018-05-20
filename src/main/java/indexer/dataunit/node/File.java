package indexer.dataunit.node;

import indexer.dataunit.table.Table;

public class File implements Node {

    private String name;
    private String url; //relative to the root of project

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File(String name, String url) {
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

    @Override
    public String toString() {
        return "File{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
