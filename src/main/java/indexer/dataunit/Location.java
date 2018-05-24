package indexer.dataunit;

public class Location {
    public int lineNumber;
    public String path;
    public String scope;//class, package, import, global

    public Location(int lineNumber, String path) {
        this.lineNumber = lineNumber;
        this.path = path;
        this.scope = "-";
    }

    @Override
    public String toString() {
        return "文件" + path + "#行" + lineNumber + " 范围: " + scope;
    }
}
