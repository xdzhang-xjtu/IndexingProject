package indexer.dataunit;

public class Location {
    public int lineNumber;
    public String path;

    public Location(int lineNumber, String path) {
        this.lineNumber = lineNumber;
        this.path = path;
    }

    @Override
    public String toString() {
        return "文件" + path + "#行" + lineNumber;
    }
}
