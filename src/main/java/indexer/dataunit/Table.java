package indexer.dataunit;

import java.util.HashMap;

public class Table  {

    /*
key name, value, line NO.
*/
    private HashMap<String, String> table;

    public Table(HashMap<String, String> table) {
        this.table = table;
    }

    public HashMap<String, String> getTable() {
        return table;
    }

    public void setTable(HashMap<String, String> table) {
        this.table = table;
    }

    public String getLineNumber(String methodname) {
        Object content = table.get(methodname);
        if (content == null)
            return "";
        else
            return (String) content;
    }
}
